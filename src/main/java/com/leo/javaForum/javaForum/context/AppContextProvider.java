package com.leo.javaForum.javaForum.context;


import com.leo.javaForum.javaForum.logger.AppLogger;
import com.leo.javaForum.javaForum.repositories.*;
import com.leo.javaForum.javaForum.repositories.connetion.DatabaseConnector;
import com.leo.javaForum.javaForum.repositories.connetion.PostgresqlConnector;
import com.leo.javaForum.javaForum.repositories.implementation.*;
import com.leo.javaForum.javaForum.services.*;
import com.leo.javaForum.javaForum.services.implementation.*;

import java.util.logging.Logger;

public class AppContextProvider {
    public static Context getFilledContext() {
        Context context = SimpleContext.getInstance();
        setupWebContext(context);

        return context;
    }

    private static void setupWebContext(Context ctx) {
        Logger logger = AppLogger.getLogger();
        ctx.putBean(Logger.class, logger);

        logger.info("Initializing application context");
        DatabaseConnector connector = new PostgresqlConnector();

        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepIml(connector);
        CommentRepository commentRepository = new CommentRepImpl(connector);
        PostRepository postRepository = new PostRepImpl(connector);
        UserRepository userRepository = new UserRepImpl(connector);
        ReactionRepository reactionRepository = new ReactionRepImpl(connector);
        UserProfileRepository userProfileRepository = new UserProfileRepIml(connector);


        AuthentificationService authentificationService = new AuthintificationServImpl(userRepository);
        CategoryService categoryService = new CategoryServiceImpl(categoryInfoRepository);
        CommentService commentService = new CommentServImpl(commentRepository, reactionRepository);
        PostService postService = new PostServImpl(postRepository);
        UserService userService = new UserServiceImpl(userProfileRepository, userRepository);




        logger.info("Created all necessary objects");
        ctx.putAll(connector, categoryInfoRepository, commentRepository, postRepository, userRepository,
                reactionRepository, userProfileRepository, authentificationService, categoryService, commentService, postService, userService);
        logger.info("Initialized application context");
    }

    public void closeContext() {
        Context context = SimpleContext.getInstance();
        context.getBean(DatabaseConnector.class).closeConnection();
    }
}
