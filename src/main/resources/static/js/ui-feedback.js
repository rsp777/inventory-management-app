(function (window) {
    'use strict';

    function hasSwal() {
        return typeof window.Swal !== 'undefined' && window.Swal;
    }

    function toast(icon, message, title) {
        if (!message) {
            return;
        }

        if (hasSwal()) {
            window.Swal.fire({
                toast: true,
                position: 'top-end',
                icon: icon,
                title: title || message,
                text: title ? message : undefined,
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true
            });
            return;
        }

        if (icon === 'error') {
            console.error(message);
        } else {
            console.log(message);
        }
    }

    var ui = {
        success: function (message) {
            toast('success', message);
        },
        error: function (message) {
            toast('error', message);
        },
        warning: function (message) {
            toast('warning', message);
        },
        info: function (message) {
            toast('info', message);
        }
    };

    window.InventoryUI = ui;
    window.showNotification = window.showNotification || ui.success;
    window.showError = window.showError || ui.error;
})(window);
