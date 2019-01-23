FROM python:3.7.2-alpine

RUN pip3 install requests
RUN pip3 install kafka-python

WORKDIR /home/test-harness
COPY . /home/test-harness

CMD python main.py
