
<%--
  Authors: James DeCarlo, and Jose Rodriguez

--%>

<footer class="page-footer">
    <div class="footer-copyright">
        <div class="container">
            &copy; 2017 Copyright
            <div class="valign-wrapper right" id="recommended-browsers">
                <div id="supported-browsers">
                    <p class="left">Needed &nbsp;</p>
                    <a href="https://www.mozilla.org/en-US/firefox/new/"><img width="28px" src="/assets/images/Firefox.ico"/></a>
                    <a href="https://www.google.com/chrome/browser/features.html?brand=CHBD&gclid=Cj0KEQjw6LXIBRCUqIjXmdKBxZUBEiQA_f50PtxL95wzJx2agcw2AVoKGjis9NZTzuEzdOL_mgcZSj8aAp6v8P8HAQ&dclid=CK3YlYXm29MCFau5swodrwUEkg"><img width="28px" src="/assets/images/Chrome.ico"/></a>
                </div>
                <a class="grey-text text-lighten-4 right" href="mailto:info@mastermycourse.com">&nbsp;Contact us</a>
            </div>
        </div>
    </div>
</footer>

<script src="/assets/js/jquery.browser.js"></script>
<script>
    if(!$.browser.chrome && !$.browser.mozilla){
        console.log("Browser not supported");
        $('#supported-browsers').show();

    } else {
        $('#supported-browsers').hide();
    }
</script>