$(function() {
    $("#showPanelsStayOpen").on("click", function() {
        let panelsStayOpen = document.getElementById("panelsStayOpen-collapseOne");
        if (panelsStayOpen.classList.contains("show")) {
            panelsStayOpen.classList.remove("show");
        } else {
            panelsStayOpen.classList.add("show");
        }
    });
    $("#showPanelsStayOpen1").on("click", function() {
        let panelsStayOpen = document.getElementById("panelsStayOpen-collapseTwo");
        if (panelsStayOpen.classList.contains("show")) {
            panelsStayOpen.classList.remove("show");
        } else {
            panelsStayOpen.classList.add("show");
        }
    });
})

