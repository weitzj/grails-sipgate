package grails.plugins.sipgate

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import groovy.net.xmlrpc.XMLRPCServerProxy

class SipgateService {

    static transactional = false

    /**
     *
     * @param phoneNumber Phonenumber in E164 Format, e.g. 4917712345678 or like +49-17712345678
     * @param smsText SmsText to send.
     * @return true if sending was successful. Otherwise false
     */
    def sendSMS(phoneNumber, smsText) {
        try {

            def serverURL = 'https://samurai.sipgate.net/RPC2'
            def clientName = 'sipgateAPI-sms.groovy'
            def clientVersion = '1.0'
            def clientVendor = 'Jan Weitz'


            //if config not given, abort
            if(!ConfigurationHolder.config.grails?.plugins?.sipgate?.username) throw new Exception('Sipgate username missing')
            if(!ConfigurationHolder.config.grails?.plugins?.sipgate?.password) throw new Exception('Sipgate password missing')
            if(!ConfigurationHolder.config.grails?.plugins?.sipgate?.password) throw new Exception('Sipgate phoneNumber missing')
            
            /**
             * Idea:
             * 1. Inititalize XMLRPC-Client
             * 2. Register Client with XMLRPC-Server
             * 3. Make sendSMS Request
             * 4. return true
             *
             * If anything fails, throw an error.
             * This will result in return false
             */


            // 1. Inititalize XMLRPC-Client
            def serverProxy = new XMLRPCServerProxy(serverURL)
            serverProxy.setBasicAuth(ConfigurationHolder.config.grails.plugins.sipgate.username, ConfigurationHolder.config.grails.plugins.sipgate.password)

            // 2. Register Client with XMLRPC-Server
            def registerRequest = [:]
            registerRequest['ClientName'] = clientName
            registerRequest['ClientVersion'] = clientVersion
            registerRequest['ClientVendor'] = clientVendor
            def registerResult = serverProxy.samurai.ClientIdentify(registerRequest)
            if(!registerResult) throw new Exception('Client registration failed')
            if(registerResult['StatusCode'] != 200) throw new Exception('Client registration failed')

            // 3. Make sendSMS Request
            //remove all dashes and + signs from phoneNumber
            phoneNumber = (phoneNumber =~ /-/).replaceAll('')
            phoneNumber = (phoneNumber =~ /\+/).replaceAll('')


            def sendSMSRequest = [:]

            //remove all dashes and + signs from localUri
            def localUri = (ConfigurationHolder.config.grails.plugins.sipgate.phoneNumber =~ /-/).replaceAll('')
            localUri = (localUri =~ /\+/).replaceAll('')

            sendSMSRequest['LocalUri'] = "sip:${localUri}@sipgate.net"
            sendSMSRequest['RemoteUri'] = "sip:${phoneNumber}@sipgate.net"
            sendSMSRequest['TOS'] = 'text'
            sendSMSRequest['Content'] = smsText

            def sendResult = serverProxy.samurai.SessionInitiate(sendSMSRequest)
            if(!sendResult) throw new Exception('Sending SMS failed')
            if(sendResult['StatusCode'] != 200) throw new Exception('Sending SMS failed')

            //we made it to the end, so this was a success
            return true

        } catch (Exception e) {
            log.error e.message
            return false
        }
    }
}
