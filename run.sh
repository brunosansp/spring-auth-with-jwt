# Certifique-se de ter o docker e o docker-compose instalados e rodando na máquina
# Um arquivo docker-compose.yml com o mínimo de configuração
version: '3'

services:
  postgres:
    image: postgres:latest
    environment:
      POSTGRES_DB: mydatabase
      POSTGRES_USER: myuser
      POSTGRES_PASSWORD: mypassword
    ports:
      - "5432:5432"

  pgadmin:
    image: dpage/pgadmin4
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@example.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    depends_on:
      - postgres


# executar o comando no diretório do arquivo docker-compose.yml para iniciar os containers
docker-compose up -d

# A URI http://localhost:5050 irá abrir o pgAdmin, use as credenciais definidas no docker-compose.yml
# No pgAdmin, vá para Servers > Add New Server. Preencha os detalhes do servidor PostgreSQL usando as informações do serviço PostgreSQL no arquivo docker-compose.yml.

# Host name/address: postgres
# Port: 5432
# Username: myuser (ou o que você definiu)
# Password: mypassword (ou o que você definiu)
# Clique em Save para adicionar o servidor PostgreSQL.