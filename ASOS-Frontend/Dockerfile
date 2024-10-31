FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build
WORKDIR /app

COPY ASOS-Frontend.sln ./
COPY ./ASOS-Frontend/ASOS-Frontend.csproj ./ASOS-Frontend/

RUN dotnet restore
COPY . ./
RUN dotnet publish -c Release -o out

FROM nginx:1.21.3-alpine
WORKDIR /app
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/out/wwwroot /usr/share/nginx/html

EXPOSE 80