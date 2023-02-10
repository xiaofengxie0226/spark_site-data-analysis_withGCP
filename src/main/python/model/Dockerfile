FROM gcr.io/deeplearning-platform-release/base-cpu

ENV APP_HOME /app
WORKDIR $APP_HOME

COPY prophet.py /app/prophet.py
COPY requirements.txt /app/requirements.txt

RUN apt-get update -y
RUN python -m pip install --upgrade pip
RUN pip install -r requirements.txt

ENTRYPOINT ["python", "prophet.py"]