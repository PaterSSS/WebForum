package com.leo.javaForum.javaForum.models.DTOs;

//не очень нравится что вываливается userId был сайт где элегантным методом писали dto
public record SubscriptionDTO(int userId, int postId, String subscriptionTime) {
}
