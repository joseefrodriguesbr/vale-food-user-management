# Projeto Final da Disciplina

# Pós-Graduação em Desenvolvimento Mobile e Cloud Computing – Inatel
## Desenvolvimento de Web Services com segurança em Java no Google App Engine

## Projeto Final da Disciplina
Implementação de uma aplicação Spring boot para cadastros de usuários;

### 👤 Autor: 
José Enderson Ferreira Rodrigues   
jose.rodrigues@pg.inatel.br, jose.e.f.rodrigues.br@gmail.com

## 📌 Implementação
Microserviço em Java/Spring Boot que expõe um CRUD (create, read, update e delete) para a entidade User.

### Requisitos atendidos:
✅ **O serviço de users expondo API para que os usuários consigam buscar as "novidades" (Novo Requisito)**

✅ CRUD (create, read, update e delete) para a entidade Promotion

✅ Operações expostas via REST seguindo os padrões HTTP para request e response

✅ Rotas do CRUD exigindo o token gerado pelo auth

✅ Projeto seguindo o padrão de rotas e controllers para as operações


## 📌 Repositórios relacionados
### vale-food-auth:
https://github.com/joseefrodriguesbr/vale-food-auth

### vale-food-promo-management:
https://github.com/joseefrodriguesbr/vale-food-promo-management

### vale-food-restaurant-management:
https://github.com/joseefrodriguesbr/vale-food-restaurant-management

## 📌 Detalhamento da solução

### ⚙️ Porta da API: 8080

### ⚙️ Variáveis importantes em application.properties:
```
spring.application.name=vfu
spring.profiles.active=test

vale-food.restaurant.url=http://localhost:8081/valefood/users
vale-food.auth.url=http://localhost:8082/valefood/users
vale-food.promo.url=http://localhost:8083/valefood/users

server.port=8080
```

### ⚙️ Rotas:

🌐 **POST /valefood/users**  
* **Descrição:** Insere uma novo usuário.  
* **Body esperado(exemplo):**
```
{
    "name": "ed",
    "email": "ed@inatel.br",
    "password": "admin",
    "type": "REGULAR",
    "preferredCategories":["Massas","Carnes"]
}
```
🔐 **Rotas protegidas** (com JWT via AuthController.verificaJWT):  
As rotas abaixo estão dentro do prefixo /estoque, e requerem autenticação JWT.  

🌐 **PUT /valefood/users/:IdUser**  
* **Descrição:** Atualiza um usuário
* * **Parâmetro de rota:**  
  * **:IdPromocao** : Id do usuário a ser atualizado
* **Body esperado(exemplo):**
```
{
    "name": "ed",
    "email": "ed@inatel.br",
    "password": "admin",
    "type": "RESTAURANT",
    "preferredCategories":["Massas","Salgado"]
}
```

🌐 **DELETE /valefood/users/:IdUser**  
* **Descrição:** deleta um usuário.
* * **Parâmetro de rota:**  
  * **:IdPromocao** : Id do usuário a ser deletado


🌐 **GET valefood/users/:IdUser/recommmendedpromotions**  
* **Descrição:** Consulta promoções recomendadas para usuário informado.
* * **Parâmetro de rota:**  
  * **:IdUser** : Id do usuário a filtrar promoções recomendadas de acordo com as preferências 
* **Body esperado(exemplo):**
``` 
[
    {
        "id": "9bf8a0f6-ac52-422a-982d-55284f423d96",
        "name": "Descontos do Feriado",
        "description": "Descontos especiais em pratos selecionados para Feriado!",
        "restaurantId": "Pizzaria",
        "restaurantName": "cad9771d-29e6-4d8c-8dfb-bb678e32d088",
        "product": {
            "productId": "713980f5-2e6f-410a-a064-ea457b3da38e",
            "promotionalPrice": 2.5,
            "category": "Massas",
            "productName": "Macarronada"
        }
    },
    {
        "id": "e04b71e9-343d-4140-828a-cbcf9cc5fde1",
        "name": "Descontos do Final de semana",
        "description": "Descontos especiais em pratos selecionados para Final de semana!",
        "restaurantId": "Restaurante Cozinha da Fazenda",
        "restaurantName": "52a2c945-11d0-40b9-85fd-739f537e02bd",
        "product": {
            "productId": "8974a7c6-7046-40ac-8690-17e4a087b969",
            "promotionalPrice": 1.5,
            "category": "Carnes",
            "productName": "File"
        }
    },
    {
        "id": "2e2db2f4-2cf8-4944-9619-daefce02bbf6",
        "name": "Descontos do Feriado",
        "description": "Descontos especiais em pratos selecionados para Feriado!",
        "restaurantId": "Restaurante Cozinha da Fazenda",
        "restaurantName": "52a2c945-11d0-40b9-85fd-739f537e02bd",
        "product": {
            "productId": "622fc20a-c7c4-4c3a-a266-9845481e69eb",
            "promotionalPrice": 2.5,
            "category": "Carnes",
            "productName": "Linguiça"
        }
    }
]
``` 

🌐 **GET /valefood/users**
* **Descrição:** Lista todos os usuários cadastrados
* **Body esperado(exemplo):**
```
[
    {
        "id": "e230565e-efe2-4efd-ac0a-26b9f1635744",
        "name": "ed",
        "email": "ed@inatel.br",
        "type": "REGULAR",
        "preferredCategories": [
            "Massas",
            "Salgados"
        ]
    },
    {
        "id": "6fc24b41-cec3-4e6d-9df1-feb0619f1682",
        "name": "ana",
        "email": "ana@inatel.br",
        "type": "RESTAURANT",
        "preferredCategories": [
            "Massas",
            "Carnes"
        ]
    }
]
```

🌐 **GET /valefood/users/:IdUser**  
* **Descrição:** Consulta uma usuário por Id.
* * **Parâmetro de rota:**  
  * **:IdPromocao** : Id do usuário a ser consultado
* **Body esperado(exemplo):**
```
{
    "id": "e230565e-efe2-4efd-ac0a-26b9f1635744",
    "name": "ed",
    "email": "ed@inatel.br",
    "type": "REGULAR",
    "preferredCategories": [
        "Massas",
        "Salgados"
    ]
}
```

## 🛠️ IDE
- **Eclipse IDE for Enterprise Java and Web Developers - Version: 2025-03 (4.35.0) Build id: 20250306-0812**

## 💻 Linguagem
- **Java(Spring Boot)**
