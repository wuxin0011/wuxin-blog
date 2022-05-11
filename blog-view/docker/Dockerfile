FROM nginx:1.21.6
MAINTAINER wuxin001
LABEL email=2191377759@qq.com
RUN  rm /etc/nginx/conf.d/default.conf
RUN  rm /etc/nginx/nginx.conf
RUN  rm -rf /usr/share/nginx/html/
COPY dist/ /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
CMD ["nginx", "-g", "daemon off;"]
EXPOSE 80
