package nimblix.in.HealthCareHub.service;

import nimblix.in.HealthCareHub.model.User;
import nimblix.in.HealthCareHub.request.UserRegistrationRequest;

public interface UserService {

    User registerUser(UserRegistrationRequest request);
}
