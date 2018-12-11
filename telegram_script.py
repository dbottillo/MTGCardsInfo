import telegram  # needs to run with python3

# it requires to run `pip install python-telegram-bot`

bot = telegram.Bot(token='455430714:AAECvqeqRQ4evUVCBlsJdp3PciqjPSXSCyc')
# print(bot.get_me())
chatId = '-225997243'  # production
# chatId = '-177269196'
bot.send_message(chat_id=chatId, text="A new update is available v3.6.0: \n-Ultimate Masters")
