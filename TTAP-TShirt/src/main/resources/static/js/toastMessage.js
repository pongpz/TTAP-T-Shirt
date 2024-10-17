// Function to create a toast message
const createToast = (type, title, message, duration = 4000) => {
    const rootToast = document.getElementById("toast-root");
    if (!rootToast) {
        console.error("Toast container not found!");
        return;
    }

    const toastContainer = document.createElement("div");
    toastContainer.className = `toast__message toast__${type}`;

    const iconDiv = document.createElement("div");
    iconDiv.className = "toast__icon";
    const icon = document.createElement("i");

    switch (type) {
        case "error":
            icon.className = "fas fa-circle-xmark";
            break;
        case "success":
            icon.className = "fas fa-circle-check";
            break;
        case "warning":
            icon.className = "fas fa-triangle-exclamation";
            break;
        case "info":
            icon.className = "fas fa-info";
            break;
        default:
            icon.className = "fas fa-info";
    }

    iconDiv.appendChild(icon);
    toastContainer.appendChild(iconDiv);

    const bodyDiv = document.createElement("div");
    bodyDiv.className = "toast__body";

    const toastTitle = document.createElement("h3");
    toastTitle.className = "toast__title";
    toastTitle.innerText = title;

    const toastMessage = document.createElement("p");
    toastMessage.className = "toast__msg";
    toastMessage.innerText = message;

    bodyDiv.appendChild(toastTitle);
    bodyDiv.appendChild(toastMessage);
    toastContainer.appendChild(bodyDiv);

    const closeButton = document.createElement("div");
    closeButton.className = "toast__close";
    const closeIcon = document.createElement("i");
    closeIcon.className = "fas fa-xmark";
    closeButton.appendChild(closeIcon);

    closeButton.onclick = () => {
        clearTimeout(timeoutId);
        rootToast.removeChild(toastContainer);
    };

    toastContainer.appendChild(closeButton);
    rootToast.appendChild(toastContainer);

    const timeoutId = setTimeout(() => {
        if (rootToast.contains(toastContainer)) {
            rootToast.removeChild(toastContainer);
        }
    }, duration);
};

// Toast types
const toastError = (title, message) => createToast("error", title, message);
const toastSuccess = (title, message) => createToast("success", title, message);
const toastWarning = (title, message) => createToast("warning", title, message);
const toastInfo = (title, message) => createToast("info", title, message);

// Initialize toast container
const initToastContainer = () => {
    const rootToast = document.createElement("div");
    rootToast.id = "toast-root";
    document.body.appendChild(rootToast);
};

