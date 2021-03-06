package net.cuodex.passxapi.service;

import net.cuodex.passxapi.entity.LoginCredential;
import net.cuodex.passxapi.entity.UserAccount;
import net.cuodex.passxapi.repository.CredentialsRepository;
import net.cuodex.passxapi.repository.UserAccountRepository;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class StorageService {


    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private CredentialsRepository credentialsRepository;

    public DefaultReturnable getEntries(String sessionId) {
        UserAccount user = authenticationService.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        Set<LoginCredential> loginCredentials = user.getLoginCredentials();

        return new DefaultReturnable(HttpStatus.OK, "Account entries retrieved successfully.").addData("amount", loginCredentials.size()).addData("entries", loginCredentials);
    }


    public DefaultReturnable getEntryById(String sessionId, String id) {
        UserAccount user = authenticationService.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        Set<LoginCredential> loginCredentials = user.getLoginCredentials();

        for (LoginCredential loginCredential : loginCredentials) {
            if (loginCredential.getId().toString().equals(id)) {
                return new DefaultReturnable(HttpStatus.OK, "Account entry with id " + id + " successfully retrieved.").addData("entry", loginCredential);
            }
        }
        return new DefaultReturnable(HttpStatus.NOT_FOUND, "Account entry with id " + id + " was not found on this account.");
    }

    public DefaultReturnable addEntry(String sessionId, String serviceName, String url, String description, String email, String username, String password) {
        UserAccount user = authenticationService.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        if (serviceName == null || url == null || description == null || email == null || username == null || password == null) {
            return new DefaultReturnable(HttpStatus.BAD_REQUEST, "Not all parameters present. (Read docs)");
        }

        LoginCredential credential = new LoginCredential();
        credential.setTitle(serviceName);
        credential.setUrl(url);
        credential.setDescription(description);
        credential.setEmail(email);
        credential.setUsername(username);
        credential.setPassword(password);

        credential.setId(credentialsRepository.getNextVal());
        user.addCredential(credential);
        userAccountRepository.saveAndFlush(user);

        return new DefaultReturnable(HttpStatus.CREATED, "Entry was successfully created.").addData("entry", credential);

    }

    public DefaultReturnable deleteEntry (String sessionId, String id) {
        UserAccount user = authenticationService.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        Set<LoginCredential> loginCredentials = user.getLoginCredentials();

        for (LoginCredential loginCredential : loginCredentials) {
            if (loginCredential.getId().toString().equals(id)) {
                loginCredentials.remove(loginCredential);
                credentialsRepository.deleteLoginCredentialById(Long.valueOf(id));
                userAccountRepository.saveAndFlush(user);


                return new DefaultReturnable(HttpStatus.OK, "Entry with id " + id + " successfully deleted.");
            }
        }
        return new DefaultReturnable(HttpStatus.NOT_FOUND, "Entry with id " + id + " was not found on this account.");
    }

    public DefaultReturnable updateEntry(String sessionId, String id, String entryService, String entryUrl, String entryDescription, String entryEmail, String entryUsername, String entryPassword) {
        UserAccount user = authenticationService.getUser(sessionId);

        if (user == null)
            return new DefaultReturnable(HttpStatus.FORBIDDEN, "Session id is invalid or expired.");

        Set<LoginCredential> loginCredentials = user.getLoginCredentials();

        for (LoginCredential loginCredential : loginCredentials) {
            if (loginCredential.getId().toString().equals(id)) {
                loginCredential.setTitle(entryService);
                loginCredential.setUrl(entryUrl);
                loginCredential.setDescription(entryDescription);
                loginCredential.setEmail(entryEmail);
                loginCredential.setUsername(entryUsername);
                loginCredential.setPassword(entryPassword);

                credentialsRepository.saveAndFlush(loginCredential);
                userAccountRepository.saveAndFlush(user);

                return new DefaultReturnable(HttpStatus.OK, "Entry with id " + id + " successfully updated.").addData("entry", loginCredential);
            }
        }
        return new DefaultReturnable(HttpStatus.NOT_FOUND, "Entry with id " + id + " was not found on this account.");
    }
}
