import telegram

bot = telegram.Bot(token='455430714:AAECvqeqRQ4evUVCBlsJdp3PciqjPSXSCyc')
#print(bot.get_me())
chatId = '-225997243'
#chatId = '-177269196'
bot.send_message(chat_id=chatId, text="A new update is available v3.2.7: \n-Rivals of Ixalan\n-Duel Decks: Nissa vs. Ob Nixilis")
