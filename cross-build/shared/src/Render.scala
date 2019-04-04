import pine._
import trail._

object Render {
  def page(body: Tag[Singleton]): Tag[Singleton] =
    html"""
     <!DOCTYPE html>
     <html>
       <head>
          <meta charset="utf-8"/>
          <meta name="viewport" content="width=device-width, initial-scale=1">
         <link rel="stylesheet" href="https://cdn.rawgit.com/Chalarangelo/mini.css/v3.0.1/dist/mini-default.min.css">
         <script src="/js/demo.js" defer></script>
       </head>
       <body>
         <header>
           <a href="#" class="logo">Demo</a>
           <a href="${Routes.ui.index(())}" class="button">Home</a>
           <a href="${Routes.ui.logIn(())}" class="button">Log in</a>
         </header>
         <div id="body">$body</div>
      </body>
     </html>
    """

  def index(): Tag[Singleton] =
    html"""
      <div>Hello world!</div>
    """

  def logIn(): Tag[Singleton] =
    html"""
      <div>
        <fieldset>
          <legend>Log in</legend>
          <label for="username">Username</label>
          <input type="text" id="username" placeholder="Username"/>
          <label for="password">Password</label>
          <input type="password" id="password" placeholder="Password"/>
          <button id="logIn">Log in</button>
          <div id="errors" class="card error" style="display: none"></div>
        </fieldset>
      </div>
    """

  def notFound(): Tag[Singleton] =
    html"""
      <div>Page not found</div>
    """

  def fromUrl(path: Path): Option[Tag[Singleton]] =
    path match {
      case Routes.ui.logIn(()) => Some(logIn())
      case Routes.ui.index(()) => Some(index())
      case _                   => None
    }
}
