FROM python:3.7.2-alpine

RUN pip3 install --upgrade pip
RUN pip3 install requests
RUN pip3 install kafka-python
RUN pip3 install python-dateutil

WORKDIR /home/validation
COPY . /home/validation

CMD ["python3", "main.py"]
