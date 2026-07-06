# Credit Analysis API

API para análise de crédito empresarial desenvolvida com **Java 21** e **Spring Boot**, utilizando conceitos de **DDD**, **Arquitetura Orientada a Eventos**, **Kafka**, **Clean Code** e boas práticas de engenharia de software.

O sistema realiza todo o fluxo de análise de crédito de uma empresa, desde a validação cadastral até a geração da decisão final, publicando eventos para envio assíncrono de notificações por e-mail.

## Tecnologias

- Java 21
- Spring Boot 3
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Flyway
- Apache Kafka
- Spring for Apache Kafka
- Spring Mail
- Swagger / OpenAPI
- Docker & Docker Compose
- JUnit 5
- Mockito
- AssertJ

## Arquitetura

O projeto foi desenvolvido utilizando uma arquitetura modular baseada em domínio.

```
company
decision
integration
notification
policy
request
score

```

Fluxo principal:

```
Company
      │
      ▼
Validation (BrasilAPI)
      │
      ▼
Score Calculation
      │
      ▼
Credit Policy
      │
      ▼
Credit Decision
      │
      ▼
Kafka Event
      │
      ▼
Notification (Email)
```

Todo o fluxo de negócio permanece síncrono.

A mensageria é utilizada apenas para efeitos colaterais (envio de e-mail), evitando acoplamento desnecessário entre os módulos.

## Funcionalidades

- Cadastro de empresas
- Atualização de empresas
- Consulta de empresas
- Criação de solicitações de crédito
- Validação automática da empresa via BrasilAPI
- Cálculo automático do score
- Classificação de risco
- Avaliação das políticas de crédito
- Aprovação automática
- Rejeição automática
- Consulta da decisão de crédito
- Publicação de eventos via Kafka
- Envio assíncrono de e-mails

## Regras de negócio

### Score

| Regra                               | Pontuação |
| ----------------------------------- | --------: |
| Empresa ativa                       |      +300 |
| Mais de 5 anos de funcionamento     |      +300 |
| Receita anual acima de R$ 1.000.000 |      +400 |

Score máximo:

```
1000 pontos
```

### Classificação de risco

| Score      | Risco  |
| ---------- | ------ |
| 700 – 1000 | LOW    |
| 400 – 699  | MEDIUM |
| 0 – 399    | HIGH   |

---

### Política de crédito

LOW

- Crédito aprovado

MEDIUM

- Encaminhado para análise manual

HIGH

- Crédito rejeitado

Também ocorre rejeição automática quando o valor solicitado ultrapassa **30% do faturamento anual informado**.

## Integrações

### BrasilAPI

Responsável por validar automaticamente os dados cadastrais da empresa utilizando o CNPJ informado.

https://brasilapi.com.br

### Kafka

Utilizado para publicação do evento:

```
CreditDecisionEvent
```

Consumido pelo módulo de notificações responsável pelo envio de e-mails.

### SMTP Gmail

Utilizado para envio das notificações de decisão de crédito.

## Configuração

### Pré-requisitos

- Java 21
- Maven
- Docker
- Docker Compose

### Variáveis de ambiente

Configure as seguintes variáveis antes de iniciar a aplicação:

```bash
MAIL_USERNAME=seu_email@gmail.com

MAIL_PASSWORD=sua_senha_de_app

NOTIFICATION_RECIPIENT_EMAIL=destinatario@gmail.com
```

## Executando a aplicação

Suba a infraestrutura:

```bash
docker compose up -d
```

Execute a aplicação:

```bash
./mvnw spring-boot:run
```

Ou execute diretamente pela IDE.

## Infraestrutura

Serviços iniciados pelo Docker Compose:

- PostgreSQL
- Apache Kafka
- Kafka UI

## Documentação

Swagger

```
http://localhost:8080/swagger-ui.html
```

Kafka UI

```
http://localhost:8081
```

## Testes

Executar todos os testes

```bash
./mvnw test
```
