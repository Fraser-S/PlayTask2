package controllers

import javax.inject.Inject

import scala.concurrent.Future
import play.api.mvc.{Action, AnyContent, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import reactivemongo.api.Cursor
import models._
import models.JsonFormats._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json._
import collection._
import play.api.i18n.{I18nSupport, MessagesApi}
import reactivemongo.bson.BSONDocument


class mongoController @Inject()(val reactiveMongoApi: ReactiveMongoApi, val messagesApi: MessagesApi)
  extends Controller with MongoController with ReactiveMongoComponents with I18nSupport {

  // TODO - keep in mind you need to have mongod.exe running before attempting to play around

  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("items"))

  def list: Action[AnyContent] = Action.async { implicit result =>
    val cursor: Future[Cursor[Item]] = collection.map {
      _.find(Json.obj())
        .sort(Json.obj("created" -> -1))
        .cursor[Item]
    }
    val futureUsersList: Future[List[Item]] = cursor.flatMap(_.collect[List]())
    futureUsersList.map {
      items => Ok(views.html.Items(items, Item.createItemForm))
    }
  }

  def addItem: Action[AnyContent] = Action {
    Ok(views.html.addItem(Item.createItemForm))
  }

  def create = Action.async { implicit result =>
    val formValidationResult = Item.createItemForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      Future(BadRequest(views.html.addItem(formWithErrors)))
    }, { item =>
      val futureResult = collection.flatMap(_.insert(item))
      futureResult.map(_ => Redirect(routes.mongoController.list()))
    })
  }

  def delete: Action[AnyContent] = Action {
    Ok(views.html.deleteItem(Item.createNameForm))
  }

  def deleteItem = Action.async { implicit result =>
    val formValidationResult = Item.createNameForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      Future(BadRequest(views.html.deleteItem(formWithErrors)))
    }, { item =>
      val selector = BSONDocument("name"-> item.name)
      val futureResult = collection.flatMap(_.remove(selector, firstMatchOnly = true))
      futureResult.map(_ => Redirect(routes.mongoController.list()))
    })
  }
}

