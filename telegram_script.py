import telegram  # needs to run with python3

from telegram_script_key import *

# it requires to run `pip install python-telegram-bot`

bot = telegram.Bot(token=key)
# print(bot.get_me())
chatId = '-225997243'  # production
# chatId = '-177269196'
bot.send_message(chat_id=chatId, text="A new update is available v3.7.0: \n-Ravnica Allegiance")
