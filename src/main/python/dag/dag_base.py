"""
DAG Base

"""
import os
from abc import ABCMeta, abstractmethod
from datetime import datetime

import airflow
from airflow import DAG

DEFAULT_OWNER = 'airflow'


class DagBase(metaclass=ABCMeta):
    """
    DAGの基底クラス
    継承先でgenerate_taskを実装し、generate_dagを呼び出すことでワークフローを構築する。
    .


    :param file_name: dag_idに利用するファイル名。
    通常継承先で__file__を渡す
    :type file_name: str
    :param doc_md: DAGの説明。
    継承先で__doc__を渡し、継承先のファイルで詳細を記載する。
    :type doc_md: str
    :param schedule_interval:
    スケジュール設定。
    デフォルトではNone
    :param owner:
    DAGのオーナー
    デフォルトでは 'airflow'
    :param catchup:
    キャッチアップ設定
    デフォルトではFalse
    :param start_date:
    DAGの開始日時
    デフォルトでDAG作成時の日付が設定される

    """
    def __init__(self,
                 file_name: str,
                 doc_md: None,
                 schedule_interval=None,
                 owner=DEFAULT_OWNER,
                 catchup=False,
                 start_date=None,
                 ):
        self._dag_id = os.path.basename(file_name).replace(".pyc", "").replace(".py", "")
        self._env_id = "dev"
        self._schedule_interval = self._set_schedule_interval(schedule_interval=schedule_interval)
        self._owner = owner
        self._is_catchup = catchup
        if start_date is None:
            self._start_date = datetime.fromtimestamp(os.path.getctime(file_name)).replace(hour=0, minute=0, second=0, microsecond=0)
        else:
            self._start_date = start_date
        self._doc_md = doc_md

    def _set_schedule_interval(self, schedule_interval: str):
        """
        スケジュール設定
        本番環境以外ではNoneを設定する
        :return: str | None
        """
        if self._env_id == "prod":
            return schedule_interval
        else:
            return None

    def _set_task_retries(self):
        """
        タスクリトライ回数
        本番環境では3回
        検証環境では0回
        """
        if self._env_id == "prod":
            return 3
        else:
            return 0

    @abstractmethod
    def generate_tasks(self, dag: DAG):
        """

        :return:
        """
        pass

    def generate_dag(self) -> DAG:
        """

        :return: DAG
        """
        with DAG(
                dag_id=self._dag_id,
                start_date=self._start_date,
                schedule_interval=self._schedule_interval,
                catchup=self._is_catchup,
                doc_md=self._doc_md,
                default_args={
                    "owner": self._owner,
                    "start_date": self._start_date,
                    "retries": self._set_task_retries(),
                }
        ) as dag:
            self.generate_tasks(dag=dag)
        return dag
