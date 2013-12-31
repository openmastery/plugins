package com.newiron.auth

import com.newiron.ServiceException
import com.newiron.mixins.ErrorSupport

@Mixin(ErrorSupport)
class UserService {

    def messageSource

    /**
     * Standard method for creating a user.
     *
     * @param username Unique username
     * @param password Plaintext password. Encoding is handled under the hood.
     * @return instance of newly created User
     */
    User createUser(String username, String password) {

        User newUser = new User(username: username, password: password)

        if (!newUser.save()) {
            throw new ServiceException("User $username could not be saved.", errorsAsMap(newUser.errors, messageSource))
        }

        return newUser

    }

    /**
     * Standard method for granting an authority.
     *
     * @param user User to be granted.
     * @param authority Authority to grant. Store authorities as String constants under the Role class.
     * @see com.newiron.auth.Role
     */
    void grantAuthority(User user, String authority) {

        Role role = Role.findByAuthority(authority)

        UserRole.create(user, role, true)

    }

    /**
     * Standard method for revoking an authority.
     *
     * @param user User to be revoked.
     * @param authority Authority to revoke.
     */
    void revokeAuthority(User user, String authority) {

        Role role = Role.findByAuthority(authority)

        UserRole.remove(user, role, true)

    }

}