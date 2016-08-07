/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.reservations;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;

/**
 * A factory of messages. It is used by the internal mail server
 * 
 * @author lucastancapiano
 *
 */
public class MyMessageHandlerFactory implements MessageHandlerFactory {

	private Logger logger = getLogger(getClass());

	public MessageHandler create(MessageContext ctx) {
		return new Handler(ctx);
	}

	/**
	 * Print the message and its properties
	 * 
	 * @author lucastancapiano
	 *
	 */
	class Handler implements MessageHandler {
		MessageContext ctx;

		public Handler(MessageContext ctx) {
			this.ctx = ctx;
		}

		/**
		 * Prints the from mail
		 */
		public void from(String from) throws RejectException {
			logger.info("FROM:" + from);
		}

		/**
		 * Prints the recipient mail
		 */
		public void recipient(String recipient) throws RejectException {
			logger.info("RECIPIENT:" + recipient);
		}

		/**
		 * Prints the body of the message
		 */
		public void data(InputStream data) throws IOException {
			logger.info("MAIL DATA");
			logger.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
			logger.info(this.convertStreamToString(data));
			logger.info("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
		}

		/**
		 * Prints if the message is created
		 */
		public void done() {
			logger.info("Finished");
		}

		/**
		 * Utility convert a stream in a string
		 * 
		 * @param is
		 *            The stream to convert
		 * @return The converted string
		 */
		public String convertStreamToString(InputStream is) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				logger.error("activiti diagram", e);
			}
			return sb.toString();
		}

	}
}