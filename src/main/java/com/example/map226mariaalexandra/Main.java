package com.example.map226mariaalexandra;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendRequestValidator;
import socialnetwork.repository.database.db.FriendRequestDbRepository;
import socialnetwork.repository.database.db.FriendshipDbRepository;
import socialnetwork.repository.database.db.MessageDbRepository;
import socialnetwork.repository.database.db.Repository;
import socialnetwork.repository.database.db.UserDbRepository;
import socialnetwork.ui.Ui;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.service.Service;

public class Main {

    public static void main(String[] args) {

        Repository<Long, User> userRepo = new UserDbRepository("jdbc:postgresql://localhost:5432/Lab4",
                "postgres", "parola",new UserValidator());
        Repository<Tuple<User, User>, Friendship> friendshipRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/Lab4",
                "postgres", "parola",new FriendshipValidator());
        Repository<Tuple<User, User>, FriendRequest> friendRequestRepository = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/Lab4",
                "postgres", "parola",new FriendRequestValidator());
        Repository<Long, Message> messageRepo = new MessageDbRepository("jdbc:postgresql://localhost:5432/Lab4",
                "postgres", "parola",userRepo);

        Service service = new Service(userRepo,friendshipRepository,messageRepo, friendRequestRepository);

        Ui ui = new Ui(service);
        ui.run();
    }

}

