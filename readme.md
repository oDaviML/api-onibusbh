# API Ônibus BH

A **API Ônibus BH** é uma aplicação que fornece informações atualizadas sobre o transporte público de Belo Horizonte. Atualmente, a API permite:

- Obter as coordenadas dos ônibus em tempo real (atualizadas a cada 20 segundos).
- Consultar as linhas disponíveis.

---

## 🛠Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **MongoDB**
- **Docker/Docker Compose**

---

## 🚀 Deploy

### Clonando o Projeto

Faça o clone do repositório com o comando abaixo:

```bash
git clone https://github.com/oDaviML/api-onibusbh.git
cd api-onibusbh
```

### Utilizando Docker

1. Inicie a aplicação com o comando:

```bash
docker-compose up
```

### Ambiente de Desenvolvimento

1. Altere o perfil ativo no arquivo `application.properties` para `dev`:

```properties
spring.profiles.active=dev
```
2. Copie o conteúdo do arquivo `docker-compose.dev.yaml` para o `docker-compose.yaml` e, em seguida, execute o projeto

---

## 📖 Documentação (Swagger)

Acesse a documentação interativa dos endpoints utilizando o Swagger em:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🚧 Em Breve

- **Suporte ao formato GTFS fornecido pela BHTrans**
- **Consulta de horários previstos**
- **Consulta de pontos de ônibus**
- **Inclusão de ônibus metropolitanos**
