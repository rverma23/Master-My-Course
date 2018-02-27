/**
 * Created by JamesDeCarlo on 5/6/17.
 */

function signIn(auth2) {
    var profile = auth2.currentUser.get().getBasicProfile();
    var data = {
        state: '${sessionScope.state}',
        userName: profile.getName(),
        userEmail: profile.getEmail(),
        userId: profile.getId(),
        userImage: profile.getImageUrl()
    };

    $.redirect("/Login", data);
}

// on google client API load.
function authInit() {
    console.log("init");
    gapi.load('auth2', function() {
        auth2 = gapi.auth2.init({
            client_id: '62228642744-d2acfs521ah38phingjmpq6l0lui7oju.apps.googleusercontent.com',
            scope: 'profile'
        });

        if (auth2.isSignedIn.get()) {
            signIn(auth2);
        }

        // sign in handler
        $("#signInButton").click(function() {
            // open sign in dialog
            auth2.signIn().then(function() {
                signIn(auth2);
            });
        });
    });
}
