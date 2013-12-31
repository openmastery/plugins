hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}

// environment specific settings
environments {

    /**
     * This environment should only be used for development purposes.
     */
    development {

        dataSource {

            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''

            driverClassName = "org.h2.Driver"

            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000"

            username = "sa"
            password = ""

            pooled = true

        }

    }

    /**
     * This environment is available so you can connect to a local mysql instance.
     * This is useful for running `grails schema-export`, or debugging data-related
     * issues where just using the bootstrap is not helpful. i.e. importing data dumps.
     */
    local {

        dataSource {

            dbCreate = "create-drop"

            driverClassName = "com.mysql.jdbc.Driver"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect

            url = "jdbc:mysql://127.0.0.1:3306/${appName}"

            username = "${appName}"
            password = "${appName}"

            pooled = true

            properties {
                maxActive = -1
                minEvictableIdleTimeMillis=1800000
                timeBetweenEvictionRunsMillis=1800000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="SELECT 1"
            }

        }

    }

    /**
     * This environment should only be used for integration/functional tests, and should
     * contain all data relevant to these tests in bootstraps.
     */
    test {

        dataSource {

            dbCreate = "create-drop"

            driverClassName = "org.h2.Driver"

            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"

            username = "sa"
            password = ""

            pooled = true

        }

    }

    /**
     * This environment is used when deployed to the ec2 development branch instance
     */
    'ec2-dev' {

        dataSource {

            dbCreate = "none"

            driverClassName = "com.mysql.jdbc.Driver"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect

            url = "jdbc:mysql://127.0.0.1:3306/${appName}_dev"
            username = "${appName}_dev"
            password = "${appName}_dev"

            pooled = true

            properties {
                maxActive = -1
                minEvictableIdleTimeMillis=1800000
                timeBetweenEvictionRunsMillis=1800000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="SELECT 1"
            }

        }

    }

    /**
     * This environment is used when deployed to the ec2 stable branch instance
     */
    'ec2-stable' {

        dataSource {

            dbCreate = "none"

            driverClassName = "com.mysql.jdbc.Driver"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect

            url = "jdbc:mysql://127.0.0.1:3306/${appName}_stable"
            username = "${appName}_stable"
            password = "${appName}_stable"

            pooled = true

            properties {
                maxActive = -1
                minEvictableIdleTimeMillis=1800000
                timeBetweenEvictionRunsMillis=1800000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="SELECT 1"
            }

        }

    }

    /**
     * This is your staging and production environment. There is not a separate staging environment
     * because staging should exactly mirror production in every way.
     */
    production {

        dataSource {

            dbCreate = "none"

            driverClassName = "com.mysql.jdbc.Driver"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect

            url = "jdbc:mysql://127.0.0.1:3306/${appName}"
            username = "${appName}"
            password = "${appName}"

            pooled = true

            properties {
                maxActive = -1
                minEvictableIdleTimeMillis=1800000
                timeBetweenEvictionRunsMillis=1800000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=true
                validationQuery="SELECT 1"
            }

        }

    }

}
