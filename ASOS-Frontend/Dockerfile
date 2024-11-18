FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build
WORKDIR /app

COPY ASOS-Frontend.sln ./
COPY ./ASOS-Frontend/ASOS-Frontend.csproj ./ASOS-Frontend/

RUN dotnet restore
COPY . ./
RUN dotnet publish -c Release -o out

FROM nginx:1.21.3-alpine
WORKDIR /app

# Copy the NGINX configuration file
COPY nginx.conf /etc/nginx/nginx.conf

# Copy the self-signed certificate and key
COPY selfsigned.crt /etc/nginx/ssl/selfsigned.crt
COPY selfsigned.key /etc/nginx/ssl/selfsigned.key

# Copy the published Blazor app to the NGINX html directory
COPY --from=build /app/out/wwwroot /usr/share/nginx/html

# Expose ports 80 and 443
EXPOSE 80
EXPOSE 443