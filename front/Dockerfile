# Multi-stage build: build Angular app then serve with nginx
FROM node:18-bullseye-slim AS build
WORKDIR /app

# copy package manifests first for better caching
COPY package.json package-lock.json* ./
RUN npm install --legacy-peer-deps

# copy sources and build
COPY . .
RUN npm run build -- --configuration=production

## Production image
FROM nginx:alpine
COPY --from=build /app/dist/electricity-business-front/ /usr/share/nginx/html/
COPY nginx/default.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
