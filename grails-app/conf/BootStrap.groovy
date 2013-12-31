import com.newiron.auth.RequestMap
import com.newiron.auth.Role
import com.newiron.auth.User

class BootStrap {

    def userService

    def init = { servletContext ->

        environments {

            development {

                new RequestMap(url: '/dbconsole/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
                new RequestMap(url: '/login/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
                new RequestMap(url: '/logout/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
                new RequestMap(url: '/css/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
                new RequestMap(url: '/js/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
                new RequestMap(url: '/images/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
                new RequestMap(url: '/plugins/**', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY').save()
                new RequestMap(url: '/', configAttribute: 'IS_AUTHENTICATED_REMEMBERED').save()

                new RequestMap(url: '/requestMap/**', configAttribute: 'ROLE_ADMIN').save()

                User newiron = userService.createUser('newiron', 'admin')
                User user = userService.createUser('user', 'password')

                Role admin = new Role(authority: 'ROLE_ADMIN').save()

                userService.grantAuthority(newiron, admin.authority)

            }

        }

    }

    def destroy = {

    }

}
