Program collects values of currencies from external xml file, analyzes collected data, creates sell / buy charts and sends report to user's e-mail.
Program works in windows cmd and linux cli. It is perfect to run in cron e.g. once a day in both modes.

This application can be run in two ways:

1) collect mode
in this mode, program gets currencies from xml file and saves them to collected_data.csv file.
You can run program in this mode using below parameters:
--currency=usd,eur --mode=collect --email=your@email.com

2) analyze mode
in this mode, program compares collected values from above point.
User gets sell and buy charts and information about current currencies via e-mail.
When value reaches minimum or maximum between time period defined in config file, it will also notify user via e-mail.
You can run program in this mode using below parameters:
--currency=usd,eur --mode=analyze --email=your@email.com

You can provide link to xml file with xpath to currencies and other parameters in config.txt file by creating it in main directory of program based on below example or you will be asked for them during first run of program.

Example content of config.txt file:
currency_xml_source;http://api.nbp.pl/api/exchangerates/tables/c?format=xml
xpath_to_currency;/ArrayOfExchangeRatesTable/ExchangeRatesTable/Rates/Rate
currency_symbol_node_name;Code
currency_sell_node_name;Ask
currency_buy_node_name;Bid
days_to_compare;1825,1460,1095,730,365,180,90,30,14,7,3
from_email;currencyMonitor@raspberrypi
external_smtp_server;0
external_smtp_host;localhost
smtp_login;currencyMonitor@raspberrypi
smtp_password;password
smtp_port;587
startls;0

