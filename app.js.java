const CLIENT_ID = 'YOUR_CLIENT_ID.apps.googleusercontent.com'; // Твій Client ID з Google Cloud
const API_KEY = 'YOUR_API_KEY'; // Твій API ключ
const SPREADSHEET_ID = '11ghgAx2V-18WdxsppqBggnjsSFHAtTLtXZ1PKR7yCkw'; // Твій Spreadsheet ID
const RANGE = 'Sheet1!I2:K'; // Діапазон для чек-листа

// Ініціалізація API
function initApiClient() {
    gapi.client.init({
        apiKey: API_KEY,
        clientId: CLIENT_ID,
        discoveryDocs: ["https://sheets.googleapis.com/$discovery/rest?version=v4"],
        scope: "https://www.googleapis.com/auth/spreadsheets.readonly"
    }).then(function () {
        // Авторизація та отримання даних
        gapi.auth2.getAuthInstance().isSignedIn.listen(updateSigninStatus);
        updateSigninStatus(gapi.auth2.getAuthInstance().isSignedIn.get());
    });
}

// Авторизація через Google OAuth 2.0
function handleAuthClick(event) {
    gapi.auth2.getAuthInstance().signIn();
}

// Обробка стану авторизації
function updateSigninStatus(isSignedIn) {
    if (isSignedIn) {
        loadSheetData();
    } else {
        document.getElementById('authorize-button').style.display = 'block';
    }
}

// Завантаження даних з Google Sheets
function loadSheetData() {
    gapi.client.sheets.spreadsheets.values.get({
        spreadsheetId: SPREADSHEET_ID,
        range: RANGE,
    }).then(function (response) {
        const data = response.result.values;
        displayChecklist(data);
    }, function (error) {
        console.error('Помилка завантаження даних: ', error);
    });
}

// Виведення чек-листа на сторінку
function displayChecklist(data) {
    const container = document.getElementById('checklist-container');
    container.innerHTML = '';
    data.forEach(function (row) {
        const task = row[0];  // Назва завдання (стовпець I)
        const isDone = row[2] === 'TRUE';  // Статус виконання (стовпець K)

        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.checked = isDone;
        checkbox.disabled = true;

        const label = document.createElement('label');
        label.textContent = task;

        const item = document.createElement('div');
        item.classList.add('task');
        item.appendChild(checkbox);
        item.appendChild(label);

        container.appendChild(item);
    });
}

// Завантаження бібліотеки Google API
function loadClient() {
    gapi.load('client:auth2', initApiClient);
}

// Подія авторизації
document.getElementById('authorize-button').onclick = handleAuthClick;

// Завантажити клієнтський API
loadClient();