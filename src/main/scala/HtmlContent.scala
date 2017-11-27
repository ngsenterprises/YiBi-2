package com.ngs.yibi.html

import com.ngs.yibi.baseconfig._

trait HtmlContent {

  //def conf: BaseConf

  def getDefault( title: String ): String = {
    s"""
    <!DOCTYPE html>
      <html >
        <head>
          <meta charset="utf-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
              <meta name="description" content="YiBi - Shorten urls.">
                <meta name="keywords" content="YiBi, Shorten urls.">
                  <title>${title} - Shorten urls</title>
        </head>
        <body>
          <header class="globalHeader " role="banner">
          </header>

          <section class="globalContent" role="main">

            <div class="wrapper x-med">
              <h2 class="YiBiLogoTagline">YiBi Url Shortener</h2>

              <section class="shortenLink">
              <h3 class="-title">Make your links manageable</h3>

              <form action="/urls" method="POST" class="shortenUrlForm" id="longUrlForm">
                <input type="text" name="url" value="" style="width: 300pt" placeholder="Paste your URL and shrink it.">
                <input type="submit" value="Shrink">
              </form>

          </section>
        </div>
      </section>

      <footer class="globalFooter">
        <div class="wrapper x-lrg">
          <nav class="-smallPrint">
            <a href="/api-docs">Developer API</a>
            <a href="https://YiBi.com/legal/terms">Terms &amp; Conditions</a>
            <a href="https://YiBi.com/legal/privacy">Privacy Policy</a>
            <a href="https://YiBi.com/legal/copyright">Copyright</a>
          </nav>
          <span class="-copyright">&copy;2017 <a href="https://YiBi.com"> YiBi LLC.</a> All Rights Reserved.</span>
        </div>
      </footer>

    </body>
    </html>
   """
  }

  def getResult( title: String, urlShort: String ): String = {
    s"""
    <!DOCTYPE html>
      <html >
        <head>
          <meta charset="utf-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
              <meta name="description" content="YiBi - Shorten urls.">
                <meta name="keywords" content="YiBi, Shorten urls.">
                  <title>${title} ${urlShort} </title>
        </head>
        <body>
          <header class="globalHeader " role="banner">
          </header>

          <section class="globalContent" role="main">

            <div class="wrapper x-med">
              <h2 class="YiBiLogoTagline">YiBi Url Shortener</h2>

              <section class="shortenLink">
              <h3 class="-title">Make your links manageable</h3>

              <input type="text" id="returnUrl" value="${urlShort}" class="link" style="width: 100pt" />

              <form action="/urls" method="POST" class="shortenUrlForm" id="longUrlForm">
                <input type="text" name="url" value="" style="width: 300pt" placeholder="Paste your URL and shrink it.">
                <input type="submit" value="Shrink">
              </form>

          </section>
        </div>
      </section>

      <footer class="globalFooter">
        <div class="wrapper x-lrg">
          <nav class="-smallPrint">
            <a href="/api-docs">Developer API</a>
            <a href="https://YiBi.com/legal/terms">Terms &amp; Conditions</a>
            <a href="https://YiBi.com/legal/privacy">Privacy Policy</a>
            <a href="https://YiBi.com/legal/copyright">Copyright</a>
          </nav>
          <span class="-copyright">&copy;2017 <a href="https://YiBi.com"> YiBi LLC.</a> All Rights Reserved.</span>
        </div>
      </footer>

    </body>
    </html>
   """
  }

}
