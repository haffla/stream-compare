package controllers

import models.auth.{Helper, IdentifiedBySession}
import models.service.oauth.OauthRouting
import models.util.{Constants, TextWrangler}
import play.api.mvc.{Controller, Cookie}

abstract class StreamingServiceController extends Controller {
  val redirectionService:OauthRouting
  val serviceName:String
  val keyCode:String = "code"
  val serviceSupportsCSRFProtection:Boolean = true
  def requestUserData(code:String, identifier:Either[Int,String]):Unit
  val keyState = "state"

  def login = IdentifiedBySession { implicit request =>
    val state = TextWrangler.generateRandomString(16)
    val withState = redirectionService.queryString + (keyState -> Seq(state))
    Redirect(redirectionService.authorizeEndpoint, withState)
      .withCookies(Cookie(redirectionService.cookieKey, state))
  }

  def callback = IdentifiedBySession { implicit request =>
    val identifier = Helper.getUserIdentifier(request.session)
    val state = request.getQueryString(keyState)
    val stateCookie = request.cookies.get(redirectionService.cookieKey)
    val validRequest:Boolean = if(serviceSupportsCSRFProtection) {
      TextWrangler.validateState(stateCookie, state)
    } else true
    if(validRequest) {
      request.getQueryString(keyCode) match {
        case Some(code) =>
          requestUserData(code,identifier)
          Redirect(routes.CollectionController.index(serviceName))
        case None =>
          Redirect(routes.CollectionController.index())
            .flashing("message" -> Constants.missingOAuthCodeError)
      }
    }
    else {
      Ok(Constants.stateMismatchError)
    }
  }
}
