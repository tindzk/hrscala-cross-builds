object Validations {
  def logIn(username: String, password: String): List[String] =
    (if (username.isEmpty) List("Username cannot be empty")
     else List()) ++
      (if (password.isEmpty) List("Password cannot be empty")
       else List()) ++
      (if (username.nonEmpty && username == password)
         List("Username and password must be different")
       else List())
}
