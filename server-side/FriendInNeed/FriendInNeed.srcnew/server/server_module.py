'''
Server for web crawler instance
@author: Sergii Kozyrev
'''
#from buildANN import *
from buildANN import make_training_data
from buildANN import set_testing_data
from buildANN import buildNetwork
from buildANN import get_percent_error
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from buildANN import SoftmaxLayer
from buildANN import BackpropTrainer
from buildANN import training_ann
import cgi
import json

class BuildANNHandler(BaseHTTPRequestHandler):
    
    
    def do_POST(self):
        global trndata, tstdata, fnn, trainer
        warning = None
        # Parse the form data posted
        form = cgi.FieldStorage(
            fp=self.rfile, 
            headers=self.headers,
            environ={'REQUEST_METHOD':'POST', 'CONTENT_TYPE':self.headers['Content-Type'],})

        # Begin the response
        
        #print fp.to_string()
        # Echo back information about what was posted in the form
        
        for field in form.keys():
            field_item = form[field]
            if field_item.filename:
                # The field contains an uploaded file
                file_data = field_item.file.read()
                file_len = len(file_data)
                print file_len
                print file_data
                if isinstance(file_data, str):
                    #insteed of del file_data we will fill tstdata
                    tstdata=set_testing_data(json.loads(file_data))
                    tstdata._convertToOneOfMany( )
                    print get_percent_error(trainer, tstdata, trndata)
                    if get_percent_error(trainer, tstdata, trndata) < 50.0:
                        #oooops! it is issue
                        print tstdata
                        print get_percent_error(trainer, tstdata, trndata)
                        self.send_response(300)
                        self.end_headers()
                    else:
                        #it is not issue
                        self.send_response(404)
                        self.end_headers()
            else:
                # no values in form
                self.wfile.write(405)
                self.end_headers()
        return
    
        
    '''
    def do_GET(self):
        try:
            global wc_dev, wc_itpro, dev_index, itpro_index
            self.send_response(200)
            self.send_header('Content-type','text/html')
            self.end_headers()
            prof, key = self.parse_params();
            if prof == "dev":
                self.wfile.write(generate_json(wc_dev.lookup(dev_index, key)))
            elif prof == "itpro":
                self.wfile.write(generate_json(wc_itpro.lookup(itpro_index, key)))
         
        except IOError:
            self.send_error(404,'File Not Found: %s' % self.path)
    '''
    '''
    def parse_params(self):
        qprof = self.path[self.path.find("=")+1 : self.path.find("&")]
        keyword = self.path[self.path.rfind("=")+1: :]
        return qprof, keyword
    '''
    


trndata = None
tstdata = []
fnn = None
trainer = None

def main():
    try:
        global trndata, tstdata, fnn, trainer
        trndata = make_training_data()
        trndata._convertToOneOfMany( )
        #tstdata = set_testing_data()
        #converting_data(trndata, tstdata)
        fnn = buildNetwork( 21, 2, trndata.outdim, outclass=SoftmaxLayer )
        trainer = BackpropTrainer( fnn, dataset=trndata, momentum=0.1, verbose=True, weightdecay=0.01)
        training_ann(trainer)
        #global wc_dev, wc_itpro, dev_index,itpro_index
        #wc_dev = WebCrawler()
        #wc_itpro = WebCrawler()
        #dev_index=wc_dev.crawl_web("dev")
        #itpro_index=wc_itpro.crawl_web("itpro")
        server = HTTPServer(('172.29.8.56', 8080), BuildANNHandler)
        print 'started http server...'
        server.serve_forever()
    except KeyboardInterrupt:
        print 'shutting down server'
        server.socket.close()

if __name__ == '__main__':
    main()