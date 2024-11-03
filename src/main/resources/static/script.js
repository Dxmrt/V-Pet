document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const showRegisterForm = document.getElementById('show-register-form');
    const showLoginForm = document.getElementById('show-login-form');
    const mainContent = document.getElementById('main-content');
    const userOptions = document.getElementById('user-options');
    const petDisplay = document.getElementById('pet-display');

    const createPetForm = document.getElementById('createPetForm');
    const logoutBtn = document.getElementById('logout-btn');
    const createPetBtn = document.getElementById('create-pet-btn');
    const cancelCreatePetBtn = document.getElementById('cancel-create-pet-btn');
    const createPetFormContainer = document.getElementById('create-pet-form');

    const myPetsButton = document.getElementById('my-pets-btn');
    const allPetsButton = document.getElementById('all-pets-btn');
    const petsContainer = document.getElementById('pets-container');

    const deletePetForm = document.getElementById('delete-pet-form');
    const deletePetButton = document.getElementById('delete-pet-button');
    const deletePetOption = document.getElementById('delete-pet-option');
    const cancelDeletePetBtn = document.getElementById('cancel-delete-pet-btn');

    let userRole;

    showRegisterForm.addEventListener('click', function() {
        loginForm.style.display = 'none';
        registerForm.style.display = 'block';
    });

    showLoginForm.addEventListener('click', function() {
        registerForm.style.display = 'none';
        loginForm.style.display = 'block';

        // Limpiar campos de usuario y contraseña al mostrar el formulario de login
            document.getElementById('login-username').value = '';
            document.getElementById('login-password').value = '';
    });

    logoutBtn.addEventListener('click', () => {
        mainContent.style.display = 'none';
        loginForm.style.display = 'block';
        petDisplay.innerHTML = '';
        petsContainer.innerHTML = '';
        localStorage.removeItem('token');

        // Limpiar campos de usuario y contraseña al cerrar sesión
            document.getElementById('login-username').value = '';
            document.getElementById('login-password').value = '';
    });

    createPetBtn.addEventListener('click', () => {
        mainContent.style.display = 'none';
        createPetFormContainer.style.display = 'block';
    });

    cancelCreatePetBtn.addEventListener('click', () => {
        createPetFormContainer.style.display = 'none';
        mainContent.style.display = 'block';
    });

    deletePetOption.addEventListener('click', () => {
        mainContent.style.display = 'none';
        deletePetForm.style.display = 'block';
    });

    cancelDeletePetBtn.addEventListener('click', () => {
        deletePetForm.style.display = 'none';
        mainContent.style.display = 'block';
    });

   // Actualización en el formulario de login
   loginForm.addEventListener('submit', function(event) {
       event.preventDefault();
       const username = document.getElementById('login-username').value.toLowerCase(); // Convertir a minúsculas
       const password = document.getElementById('login-password').value;

       fetch('/virtualpet/login', {
           method: 'POST',
           headers: {
               'Content-Type': 'application/json',
           },
           body: JSON.stringify({
               userName: username, // Nombre de usuario en minúsculas
               userPassword: password,
           }),
       })
       .then(response => {
           if (!response.ok) {
               throw new Error('Credenciales no validas');
           }
           return response.text();
       })
       .then(token => {
           localStorage.setItem('token', token);

           // Solicitar el rol del usuario
           return fetch('/virtualpet/role', {
               method: 'GET',
               headers: {
                   'Authorization': 'Bearer ' + token
               }
           });
       })
       .then(response => response.json())
       .then(roleData => {
           userRole = roleData.role;
           loginForm.style.display = 'none';
           mainContent.style.display = 'block';

           if (userRole === 'ADMIN') {
               allPetsButton.style.display = 'block';
           } else {
               allPetsButton.style.display = 'none';
           }
       })
       .catch(error => {
           alert(error.message);
       });
   });


    registerForm.addEventListener('submit', function(event) {
        event.preventDefault();
        const username = document.getElementById('register-username').value;
        const password = document.getElementById('register-password').value;
        const role = document.getElementById('register-role').value;

        fetch('/virtualpet/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userName: username,
                userPassword: password,
                userRole: role,
            }),
        })
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                alert('Registro fallido: ' + data.error);
            } else {
                alert('Registro exitoso! Ahora puedes loguearte.');
                registerForm.style.display = 'none';
                loginForm.style.display = 'block';
            }
        })
        .catch(error => {
            alert('Ha ocurrido un error: ' + error.message);
        });
    });

    // Create Pet
    createPetForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const token = localStorage.getItem('token');
        if (!token) {
            alert('Porfavor logueate primero');
            return;
        }

        const petName = document.getElementById('pet-name').value;
        const petBreed = document.getElementById('pet-breed').value;
        const petColor = document.getElementById('pet-color').value;

        const response = await fetch('/virtualpet/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ petName, petBreed, petColor })
        });

        if (response.ok) {
            alert('Mascota creada correctamente!');
            createPetFormContainer.style.display = 'none';
            mainContent.style.display = 'block';
        } else {
            alert('Fallo al crear la mascota, tienes mas de 3!');
        }
    });

    // Get pets (USER)
    myPetsButton.addEventListener('click', async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('Porfavor logueate primero.');
            return;
        }

        const response = await fetch(`/virtualpet/user/pets`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const pets = await response.json();
            displayPets(pets);
        } else {
            alert('No se ha podido encontrar la mascota');
        }
    });

    // All pets (ADMIN)
    allPetsButton.addEventListener('click', async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('Porfavor logueate primero');
            return;
        }

        const response = await fetch('/virtualpet/admin/pets', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const pets = await response.json();
            displayAllPets(pets);
        } else {
            alert('Fallo al encontrar la mascota');
        }
    });


function displayPets(pets) {
    const petDisplay = document.getElementById('pet-display');
    petDisplay.style.display = 'block';
    petDisplay.innerHTML = ''; // Limpia el contenido previo

    pets.forEach(pet => {
        const petImageContainer = document.createElement('div');
        petImageContainer.classList.add('pet-image-container');

        // Imagen de la mascota viva con ruta específica
        const petImage = document.createElement('img');
        petImage.src = `images/${pet.breed}_${pet.color}.png`;
        petImage.classList.add('pet-image');
        petImage.style.display = pet.health > 0 ? 'block' : 'none'; // Mostrar solo si la salud es mayor a 0

        // Imagen de muerte
        const deathImage = document.createElement('img');
        deathImage.src = "/images/death-flat-color-outline-icon-free-png.png";
        deathImage.classList.add('death-image');
        deathImage.style.display = pet.health <= 0 ? 'block' : 'none'; // Mostrar solo si la salud es 0

        const petInfo = document.createElement('div');
        petInfo.classList.add('pet-info-user');
        petInfo.innerHTML = `
            <h3>${pet.name}</h3>
            <p>Id: ${pet.id}</p>
            <p>Tipo: ${pet.breed}</p>
            <p>Color: ${pet.color}</p>
            <p>Felicidad: ${pet.happiness}</p>
            <p>Vida: ${pet.health}</p>
            <p>Limpieza: ${pet.cleanliness}</p>
        `;

        // Eventos para mostrar/ocultar la información de la mascota
        petImageContainer.addEventListener('mouseover', () => {
            petInfo.style.display = 'block';
        });

        petImageContainer.addEventListener('mouseout', () => {
            petInfo.style.display = 'none';
        });

        // Agregar solo la imagen correspondiente según la salud
        petImageContainer.appendChild(petImage);   // Imagen de mascota viva
        petImageContainer.appendChild(deathImage); // Imagen de muerte
        petImageContainer.appendChild(petInfo);
        petDisplay.appendChild(petImageContainer);
    });
}




    // Delete a pet
    deletePetButton.addEventListener('click', async () => {
        const token = localStorage.getItem('token');
        const petId = document.getElementById('delete-pet-id').value;

        if (!token) {
            alert('Porfavor logueate primero');
            return;
        }

        if (!petId) {
            alert('Introduce la ID de la mascota');
            return;
        }

        const response = await fetch(`/virtualpet/pet/delete/${petId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            alert('Mascota eliminada');
            deletePetForm.style.display = 'none';
            mainContent.style.display = 'block';
            petDisplay.innerHTML = ''; // clean pets
            petsContainer.innerHTML = ''; // clean pets
        } else {
            if (response.status === 403) {
                alert('No tienes permiso para borrar esta mascota');
            } else if (response.status === 404) {
                alert('Mascota no encontrada.');
            } else {
                alert('Fallo al borrar la mascota');
            }
        }
    });


    const updatePetForm = document.getElementById('update-pet-form');
    const updatePetButton = document.getElementById('update-pet-button');
    const updateField = document.getElementById('update-field');
    const updateValue = document.getElementById('update-value');
    const updateBreed = document.getElementById('update-breed');
    const updateColor = document.getElementById('update-color');
    const updatePetOption = document.getElementById('update-pet-option');
    const cancelUpdatePetBtn = document.getElementById('cancel-update-pet-btn');

    updatePetOption.addEventListener('click', () => {
        mainContent.style.display = 'none';
        updatePetForm.style.display = 'block';
    });

    cancelUpdatePetBtn.addEventListener('click', () => {
        updatePetForm.style.display = 'none';
        mainContent.style.display = 'block';
    });

    updateField.addEventListener('change', () => {
        const selectedField = updateField.value;
        updateValue.style.display = 'none';
        updateBreed.style.display = 'none';
        updateColor.style.display = 'none';

        switch (selectedField) {
            case 'change_name':
                updateValue.style.display = 'block';
                break;
            case 'change_breed':
                updateBreed.style.display = 'block';
                break;
            case 'change_color':
                updateColor.style.display = 'block';
                break;
        }
    });

    updatePetButton.addEventListener('click', async () => {
        const token = localStorage.getItem('token');
        const petId = document.getElementById('update-pet-id').value;
        const selectedField = updateField.value;
        let updateValueContent;

        if (!token) {
            alert('Porfavor logueate primero');
            return;
        }

        if (!petId) {
            alert('Porfavor introduce todos los campos requeridos');
            return;
        }

        switch (selectedField) {
            case 'change_name':
                updateValueContent = updateValue.value;
                break;
            case 'change_breed':
                updateValueContent = updateBreed.value;
                break;
            case 'change_color':
                updateValueContent = updateColor.value;
                break;
        }

        if (!updateValueContent) {
            alert('Introduce un valor a actualizar');
            return;
        }

        const response = await fetch(`/virtualpet/pet/update/${petId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ update: selectedField, change: updateValueContent })
        });

        if (response.ok) {
            alert('Mascota actualizada');
            updatePetForm.style.display = 'none';
            mainContent.style.display = 'block';
            petDisplay.innerHTML = ''; // clean pets
            petsContainer.innerHTML = ''; // clean pets
        } else {
            if (response.status === 403) {
                alert('No tienes permiso para actualizar esta mascota');
            } else if (response.status === 404) {
                alert('Mascota no encontrada.');
            } else {
                alert('Fallo al actualizar la mascota.');
            }
        }
    });


    const actionPetForm = document.getElementById('pet-action-form');
    const actionPetButton = document.getElementById('action-pet-button');
    const interactPetOption = document.getElementById('interact-pet-option');
    const cancelActionPetBtn = document.getElementById('cancel-action-pet-btn');

    interactPetOption.addEventListener('click', () => {
        mainContent.style.display = 'none';
        actionPetForm.style.display = 'block';
    });

    cancelActionPetBtn.addEventListener('click', () => {
        actionPetForm.style.display = 'none';
        mainContent.style.display = 'block';
    });

    actionPetButton.addEventListener('click', async () => {
        const token = localStorage.getItem('token');
        const petId = document.getElementById('action-pet-id').value;
        const actionField = document.getElementById('action-field').value;

        if (!token) {
            alert('Porfavor logueate primero');
            return;
        }

        if (!petId) {
            alert('Introduce la ID de la mascota');
            return;
        }

        const response = await fetch(`/virtualpet/pet/action/${petId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ action: actionField })
        });

        if (response.ok) {
            alert('Jugar, comer o bañarse se ha ejecutado correctamente!');
            actionPetForm.style.display = 'none';
            mainContent.style.display = 'block';
            petDisplay.innerHTML = ''; // clean pets
            petsContainer.innerHTML = ''; // clean pets
        } else {
            if (response.status === 403) {
                alert('No tienes permiso para interactuar con esta mascota.');
            } else if (response.status === 404) {
                alert('Mascota no encontrada.');
            } else {
                alert('Fallo al interactuar con la mascota.');
            }
        }
    });
});
