// Auto-dismiss alerts after 5 seconds
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.alert').forEach(function (alertEl) {
        setTimeout(function () {
            var alert = bootstrap.Alert.getOrCreateInstance(alertEl);
            alert.close();
        }, 5000);
    });

    // Simple client-side confirmation for destructive actions
    document.querySelectorAll('form[data-confirm]').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            if (!confirm(form.getAttribute('data-confirm'))) {
                e.preventDefault();
            }
        });
    });
});
