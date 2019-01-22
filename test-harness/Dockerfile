FROM python:latest

RUN pip3 install requests
RUN pip3 install kafka-python

WORKDIR /home/test-harness
COPY . /home/test-harness

CMD python main.py
