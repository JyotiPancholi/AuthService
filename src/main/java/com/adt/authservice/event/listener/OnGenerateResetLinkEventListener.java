/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adt.authservice.event.listener;

import java.io.IOException;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.adt.authservice.event.OnGenerateResetLinkEvent;
import com.adt.authservice.exception.MailSendException;
import com.adt.authservice.model.PasswordResetToken;
import com.adt.authservice.model.User;
import com.adt.authservice.service.MailService;

import freemarker.template.TemplateException;

@Component
public class OnGenerateResetLinkEventListener implements ApplicationListener<OnGenerateResetLinkEvent> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final MailService mailService;

	@Autowired
	public OnGenerateResetLinkEventListener(MailService mailService) {
		this.mailService = mailService;
	}

	/**
	 * As soon as a forgot password link is clicked and a valid email id is entered,
	 * Reset password link will be sent to respective mail via this event
	 */
	@Override
	@Async
	public void onApplicationEvent(OnGenerateResetLinkEvent onGenerateResetLinkMailEvent) {
		sendResetLink(onGenerateResetLinkMailEvent);
	}

	/**
	 * Sends Reset Link to the mail address with a password reset link token
	 */
	private void sendResetLink(OnGenerateResetLinkEvent event) {
		PasswordResetToken passwordResetToken = event.getPasswordResetToken();
		User user = passwordResetToken.getUser();
		String recipientAddress = user.getEmail();
		String emailConfirmationUrl = event.getRedirectUrl().queryParam("token", passwordResetToken.getToken())
				.queryParam("email", recipientAddress).toUriString();
		try {
			mailService.sendResetLink(emailConfirmationUrl, recipientAddress);
		} catch (IOException | TemplateException | MessagingException e) {
			LOGGER.error("exception: {}", e);
			throw new MailSendException(recipientAddress, "Email Verification");
		}
	}

}
