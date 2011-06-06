def configFile = new File("${basedir}/grails-app", 'conf/Config.groovy')
	if (configFile.exists()) {
        //if no config options are set so far, add them to the config-file
        def config = new ConfigSlurper().parse(configFile.text)

        if(!config?.grails?.plugins?.sipgate?.username && !config?.grails?.plugins?.sipgate?.password && !config?.grails?.plugins?.sipgate?.phoneNumber) {

            configFile.withWriterAppend {
                it.writeLine '\n// Added by the Sipgate plugin:'
                it.writeLine "grails.plugins.sipgate.username = 'YOUR_USERNAME'"
                it.writeLine "grails.plugins.sipgate.password = 'YOUR_PASSWORD'"
                it.writeLine "\n//According to E.164, e.g. '4922112345678'"
                it.writeLine "grails.plugins.sipgate.phoneNumber = 'YOUR_PHONE'"
            }
        }

        println '''
*******************************************************
* You've installed the Sipgate plugin.                *
*                                                     *
* Next edit your "conf/Config.groovy" and add your    *
* Sipgate credentials                                 *
* It should look like this:                           *
*  // Added by the Sipgate plugin:                    *
*  grails.plugins.sipgate.username = 'YOUR_USERNAME'  *
*  grails.plugins.sipgate.password = 'YOUR_PASSWORD'  *
*                                                     *
*  //According to E.164, e.g. '4922112345678'         *
*  grails.plugins.sipgate.phoneNumber = 'YOUR_PHONE'  *
*                                                     *
*                                                     *
*******************************************************
'''
       
    }
else {
        println '''
*******************************************************
* Could not find your "conf/Config.groovy" file       *
*                                                     *
* The plugin will not work unless "conf/Config.groovy *
* is present. Make sure the config file is present at *
* the location specified and re-run the plugin        *
* installation process.                               *
*                                                     *
*******************************************************
'''
    }
