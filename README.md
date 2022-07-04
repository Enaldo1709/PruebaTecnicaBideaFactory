# Book Service 🏘
Book service es un proyecto que expone una web api que permite reservar casas de un determinado alojamiento.
El servicio permite:
    - Validar las casas por reservar.\
    - Guardar las reservas en una base de datos.\
    - Utilizar codigos de descuento para la reserva de casas.\
Este proyecto está construido como parte del reto técnico para el proceso de selección del Enaldo Narvaez con Bidea Factory.

# Comenzando 💡
## Pre-Requisitos
Para poder desplegar el proyecto necesitas las siguientes dependencias:\
- Docker (testeado con version 20.10.5+dfsg1)
- Docker-Compose (v 1.25.0)
- Git\
Para instalar docker puedes segir los pasos de la documentacion de la página oficial:
- [Install Docker Desktop on Windows](https://docs.docker.com/desktop/windows/install/)
- [Install Docker Desktop on Linux](https://docs.docker.com/desktop/linux/install/)
- [Install Docker Desktop on Mac](https://docs.docker.com/desktop/mac/install/)

# Instalación 🛠
Una vez se tenga docker instalado se deben seguir los siguientes paso:\
1. Se debe clonar el repositorio en una carpeta local:

```bash
git clone https://github.com/Enaldo1709/PruebaTecnicaBideaFactory\
    && cd PruebaTecnicaBideaFactory
```
Si no se cuenta con git instalado, se puede instalar de la siguiente manera:
```bash
sudo apt install git
```
O descargandolo de la fuente pagina oficial de git: [Git - Downloads](https://git-scm.com/downloads)

2. Ya estando en la carpeta raiz del proyecto se ejecuta la tarea de build mediante el siguiente comando y se espera a que finalice.:
```bash
docker build --pull --no-cache --rm -f "deployment/service/Dockerfile" -t ms-book-service:latest "."
```

3. Una vez se haya construido la imagen de docker se despliega la apliacación de la siguiente manera:
```bash
docker compose -f "deployment/docker-compose.yml" up -d --build
```
4. Para este momento el servicio debería estar operativo y puede ser consumido a través de la ruta local: [http://localhost:8080/bideafactory/book](http://localhost:8080/bideafactory/book). 
5. Puede utilizar el cliente rest que desee (por ejemplo Postman) y realizar peticiones al servicio de acuerdo a su definición (Ver documentacion de [swagger](./ms-book-service/ms-books-service.yaml) para mas informacion). En este caso puede hacer una peticion con curl mediante el siguiente comando:
```bash
curl -X POST \
  'http://localhost:8080/bideafactory/book' \
  --header 'Content-Type: application/json' \
  --data-raw '{
    "id":"14564088-4",
    "name":"Gonzalo",
    "lastname":"Perez",
    "age":33,
    "phoneNumber":"36528837297",
    "startDate":"2022-08-08",
    "endDate":"2022-08-09",
    "houseId":"213132",
    "discountCode":"D0644A3D"
}'
```
Si recibe el siguiente mensaje quiere decir que habrá registrado su primera casa:
```json
{"statusCode":200,"message":"Book Accepted."}
```

6. Para detener el proyecto se puede utilizar los siguientes comandos:
```bash
docker compose -f 'deployment/docker-compose.yml'  -p 'deployment' stop
```
Que detiene la ejecucion de los conetendores, pero sin eliminarlos.
O:
```bash
docker compose -f 'deployment/docker-compose.yml'  -p 'deployment' down
```
Que detiene los contenedores y además los elimina.

# Construcción ⚙
El projecto está construido con las siguientes tecnologias:

- Java EE 17 version openjdk:
    - Spring Boot 2.7.1 🍃
        - Spring WebFlux
        - Spring Data R2dbc
    - Project Reactor ⚛
        - Reactor Core
        - Netty
    - Lombok 🌶
- PostgreSQL v14

# Autores 👨‍💻
### Enaldo Narvaez Yepes:
- Desarrollador del proyecto - Desarrollador Backend

# Agradecimientos ⭐
Muchas gracias a toda la comunidad opensource, principalmente a los autores de las tecnologías usadas en este proyecto. \
Tambien quiero agradecer a la empresa Bidea Factory por la oportunidad de participar en su proceso de selección.
