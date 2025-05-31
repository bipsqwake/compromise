# Этап сборки
FROM node:slim AS builder

WORKDIR /app

COPY package*.json ./
COPY . .
 
# Добавляем build-аргумент для режима vite
ARG VITE_MODE=qa

# Копируем нужный .env файл (vite поддерживает .env.production, .env.staging, .env.development)
RUN if [ -f ".env.${VITE_MODE}" ]; then cp ".env.${VITE_MODE}" .env; fi

RUN npm install
RUN npm run build -- --mode $VITE_MODE

# Этап запуска
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]