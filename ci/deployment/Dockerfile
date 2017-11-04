FROM node:6.10.3-alpine

ARG MONGO_HOST
ARG MONGO_PORT="27017"
ARG MONGO_USER
ARG MONGO_PASSWORD
ARG MONGO_DATABASE
ARG GOOGLE_LOGIN_KEY

ENV MONGO_HOST $MONGO_HOST
ENV MONGO_PORT $MONGO_PORT
ENV MONGO_USER $MONGO_USER
ENV MONGO_PASSWORD $MONGO_PASSWORD
ENV MONGO_DATABASE $MONGO_DATABASE
ENV GOOGLE_LOGIN_KEY $GOOGLE_LOGIN_KEY

CMD ["mkdir", "/home/deployment"]
#CMD ["mkdir", "/home/deployment/server"]
COPY server.tar /home/deployment/server.tar
COPY deployment.sh /home/deployment/deployment.sh
CMD ["sh", "/home/deployment/deployment.sh", "/home/deployment"]
