🐾 Virtual Pet API 🐾

Welcome to the Virtual Pet API! This project allows users to interact with a virtual pet, managing its needs to keep it happy and healthy. 
Users can authenticate, manage pets, and perform various actions to maintain their pet’s wellbeing. Administrators have additional control over all pets in the system.


📜 Project Description

The Virtual Pet API provides endpoints for creating and managing user accounts, authenticating with JWT, and allowing users to interact with their pets. Each pet has attributes like health and happiness, which users must manage to keep their pet thriving. If the pet's health drops to zero, a visual indicator—a "death icon"—will appear, signaling that the pet urgently needs to be fed to restore its health. Administrators can view and manage all pets.

🚀 Technologies Used

    Spring Boot: Framework for building REST APIs.
    Spring Security: Secures the application with JWT-based authentication and role-based access control.
    JPA (Java Persistence API): Manages data persistence in relational databases.
    Swagger and WebJars: Provides interactive API documentation.

Note: MongoDB integration is planned for future updates to support additional features.


🌈 Interface

### Login Interface
![Login Interface](images_git/loginvpet.png)

### Registration Interface
![Registration Interface](images_git/registervpet.png)

### Pet Status: Health Zero (Death Icon):
![Pet Death Icon](images_git/gambitamuerta.png)



🐾 Pet Management

Users can take care of their pets through various interactions. Key features include:

    View User Pets: GET /virtualpet/user/pets - Retrieves the list of pets associated with the authenticated user.
    Feed the Pet: POST /virtualpet/user/pets/{petId}/feed - Increases the pet's health and happiness.
    Play with the Pet: POST /virtualpet/user/pets/{petId}/play - Boosts the pet's happiness.
    

📊 Health Indicator

Each pet has a health attribute that must be maintained through regular care. When health reaches zero, the pet's icon changes to a "death icon," indicating the pet needs to be fed immediately to recover.
Pet Status: Alive

Pet Status: Health Zero (Death Icon)


👑 Admin Privileges

Admins have elevated access to manage all pets in the system:

    View All Pets: GET /virtualpet/admin/pets - Retrieves a list of all pets across users (requires admin privileges).
    

📬 Using the API with Postman

To simplify testing and interaction with the Virtual Pet API, a pre-configured Postman collection is included. This collection contains all the essential endpoints for authentication, pet management, and admin functionalities.
How to Use the Postman Collection:

    Download the Collection: Download the provided Postman collection file and save it locally.

    Import the Collection in Postman:
    
        - Open Postman.
        - Click on Import in the top-left corner of the Postman app.
        - Select the Virtual Pet API.postman_collection.json file you downloaded.
        - The collection will appear in your Postman workspace with all the endpoints organized for easy access.

    Set Up Environment Variables:
    
        - Create a new environment in Postman with the following variables:
        - base_url: The base URL of your API, e.g., http://localhost:8080 if running locally.
        - jwt_token: This will hold your JWT token after login to authenticate requests.
        
       * After logging in, copy the JWT token from the login response and paste it into the jwt_token environment variable to access protected endpoints.*
        

🛠️ Running the Project

Clone the Repository

bash

git clone https://github.com/yourusername/virtual-pet

Configure the Database

Edit application.properties to include your database URL and credentials.
Start the Application

bash

./mvnw spring-boot:run


📝 Future Enhancements

    MongoDB for Accessories: Implement MongoDB to store accessories, allowing users to personalize and enhance their pets.
    Improved Visual Feedback: Introduce more visual cues and animations for pet status changes.
    Reminder System: Add notifications to remind users to care for their pets.
