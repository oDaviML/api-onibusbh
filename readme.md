# API Ônibus BH

A **API Ônibus BH** é uma aplicação que fornece informações atualizadas sobre o transporte público de Belo Horizonte. Atualmente, a API permite:

- Obter as coordenadas dos ônibus em tempo real (atualizadas a cada 20 segundos).
- Consultar as linhas disponíveis.

---

## 🛠Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **MongoDB**

---

## 🚀 Instalação local


Faça o clone do repositório com o comando abaixo:

```bash
git clone https://github.com/oDaviML/api-onibusbh.git
cd api-onibusbh
```

Inicie a aplicação com o comando:

```bash
docker compose -f docker-compose.prod.yaml up -d
```

## 📖 Documentação (Swagger)

Acesse a documentação interativa dos endpoints utilizando o Swagger em:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🚧 Em Breve

- **Suporte ao formato GTFS fornecido pela BHTrans**
- **Consulta de horários previstos**
- **Inclusão de ônibus metropolitanos**
