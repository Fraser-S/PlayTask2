package controllers

import javax.inject.Inject

import models.Item
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

class Application @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index: Action[AnyContent] = Action {
    Redirect(routes.Application.listItems())
  }

  def listItems: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.Items(Item.items, Item.createItemForm))
  }

  def addItem: Action[AnyContent] = Action {
    Ok(views.html.addItem(Item.createItemForm))
  }

  def editItemSelection: Action[AnyContent] = Action {
    Ok(views.html.editItemSelection(Item.items, Item.createNameForm))
  }

  def createItem: Action[AnyContent] = Action { implicit request =>
    val formValidationResult = Item.createItemForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.addItem(formWithErrors))
    }, { item =>
      Item.items.append(item)
      Redirect(routes.Application.listItems())
    })
  }

  private def getIndex(item: Item ):Int ={
   Item.items.indexWhere(element => element == item)
  }

  def selectItemToEdit(): Action[AnyContent] = Action { implicit request =>
    val formValidationResult = Item.createNameForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.editItemSelection(Item.items, formWithErrors))
    }, { itemName =>
      val item = Item.items.filter(item => item.name == itemName.name).headOption
      item match {
        case Some(i) => val id = getIndex(i); Ok(views.html.editItem(i, id, Item.createItemForm.fill(Item.items(id))))
        case _ => BadRequest(views.html.editItemSelection(Item.items,Item.createNameForm))
      }
    })
  }

  def updateItem(id: Long): Action[AnyContent] = Action{ implicit request =>
    val formValidationResult = Item.createItemForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.editItem(Item.items(id.toInt), id.toInt, Item.createItemForm.fill(Item.items(id.toInt))))
    }, { item =>
      Item.items(id.toInt) = item
      Redirect(routes.Application.listItems())
    })
  }

  def deleteItemSelection(): Action[AnyContent] = Action{ implicit request =>
    Ok(views.html.deleteItem(Item.items, Item.createNameForm))
  }

  def deleteItem(): Action[AnyContent] = Action { implicit request =>
    val formValidationResult = Item.createNameForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.deleteItem(Item.items, formWithErrors))
    }, { itemName =>
      val item = Item.items.filter(item => item.name == itemName.name).headOption
      item match {
        case Some(i) => Item.items.remove(getIndex(i)); Redirect(routes.Application.listItems())
        case _ => BadRequest(views.html.deleteItem(Item.items,Item.createNameForm))
      }
    })
  }
}