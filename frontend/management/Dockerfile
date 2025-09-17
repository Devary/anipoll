FROM --platform=$BUILDPLATFORM node:20-alpine3.21 as builder
RUN mkdir /project
WORKDIR /project
RUN npm install -g @angular/cli@13
COPY package.json package-lock.json ./
RUN npm ci
EXPOSE 4100
COPY . .
CMD ["ng", "serve", "--host", "0.0.0.0"]
