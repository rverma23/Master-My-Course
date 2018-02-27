$(document).ready(function(){
    $('.carousel').carousel({dist:0, indicators: true});
    window.setInterval(function(){$('.carousel').carousel('next')},10000)
});

//sidebar

// Initialize collapse button
 $(".button-collapse").sideNav();
 // Initialize collapsible (uncomment the line below if you use the dropdown variation)
 //$('.collapsible').collapsible();

 $('.button-collapse').sideNav({
     menuWidth: 300, // Default is 300
     edge: 'right', // Choose the horizontal origin
     closeOnClick: true, // Closes side-nav on <a> clicks, useful for Angular/Meteor
     draggable: true // Choose whether you can drag to open on touch screens
   }
 );

$('.button-collapse').sideNav('show');
