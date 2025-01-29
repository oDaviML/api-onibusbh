# 🚍 API Ônibus BH

A **API Ônibus BH** é responsável por fornecer informações em tempo real sobre o transporte público de Belo Horizonte.

## 🌐 Frontend

Esta API alimenta o frontend da aplicação [Ônibus BH](https://onibusbh.netlify.app/), permitindo que usuários consultem
dados atualizados sobre o sistema de ônibus. Consulte o código-fonte do frontend
no [repositorio](https://github.com/oDaviML/onibus-bh) para obter mais detalhes.

---

## ✨ Funcionalidades

- 📊 **Localização em tempo real**: Obtenha as coordenadas dos ônibus, atualizadas a cada 20 segundos.
- 🔍 **Consulta de linhas**: Pesquise e visualize informações detalhadas sobre as linhas de ônibus.

---

## 🛠 Tecnologias Utilizadas

- **Linguagem**: Java 21
- **Framework**: Spring Boot
- **Banco de Dados**: MongoDB

---

## 🚀 Instalação Local

1. Clone o repositório:

   ```bash
   git clone https://github.com/oDaviML/api-onibusbh.git
   cd api-onibusbh
   ```

2. Inicie a aplicação utilizando Docker:

   ```bash
   docker compose -f docker-compose.prod.yaml up -d
   ```

---

## 📖 Documentação (Swagger)

Acesse a documentação interativa dos endpoints utilizando o Swagger:

- **Versão Online**: [Documentação Swagger](https://apionibusbh.davimartinslage.com.br/swagger-ui/index.html)
- **Versão Local** (após rodar a
  aplicação): [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🚧 Em Desenvolvimento

- ⚡ **Suporte ao formato GTFS fornecido pela BHTrans**
- ⏰ **Consulta de horários previstos**
- 🛣️ **Informação detalhada do trajeto de cada linha**
- 🚍 **Inclusão de ônibus metropolitanos**

---

Desenvolvido com ❤️ por [DaviML](https://github.com/oDaviML).

