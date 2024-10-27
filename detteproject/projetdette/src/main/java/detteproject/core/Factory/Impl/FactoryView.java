package detteproject.core.Factory.Impl;

import detteproject.core.View;
import detteproject.core.Factory.FactoryViewInterface;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;
import detteproject.data.entities.User;
import detteproject.services.ArticleService;
import detteproject.services.ClientService;
import detteproject.services.DetteService;
import detteproject.services.PaiementService;
import detteproject.services.UserService;
import detteproject.views.ArticleView;
import detteproject.views.ClientView;
import detteproject.views.DetteView;
import detteproject.views.UserView;
import detteproject.views.PaiementView;

public class FactoryView<T> implements FactoryViewInterface<T> {

    private final View<T> view;

    public FactoryView(Class<T> clazz, ClientService clientService, ArticleService articleService,
            UserService userService, UserView userView, DetteService detteService) {

        if (Client.class.isAssignableFrom(clazz)) {
            this.view = (View<T>) new ClientView(userView, clientService, userService);
        } else if (User.class.isAssignableFrom(clazz)) {
            this.view = (View<T>) new UserView(userService);
        } else if (Dette.class.isAssignableFrom(clazz)) {
            this.view = (View<T>) new DetteView(articleService, clientService, detteService);
        } else if (Article.class.isAssignableFrom(clazz)) {
            this.view = (View<T>) new ArticleView(articleService);
        } else if (Paiement.class.isAssignableFrom(clazz)) {
            this.view = (View<T>) new PaiementView(clientService, detteService);
        } else {
            throw new IllegalArgumentException("Unsupported entity type: " + clazz.getName());
        }
    }

    @Override
    public View<T> createView() {
        return view;
    }
}
