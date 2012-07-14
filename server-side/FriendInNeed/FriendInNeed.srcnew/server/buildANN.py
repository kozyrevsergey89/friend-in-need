import scipy
from pybrain.tools.shortcuts import buildNetwork
from pybrain.datasets import SupervisedDataSet
from pybrain.supervised.trainers import BackpropTrainer
from pybrain.datasets            import ClassificationDataSet
from pybrain.utilities           import percentError
from pybrain.tools.shortcuts     import buildNetwork
from pybrain.supervised.trainers import BackpropTrainer
from pybrain.structure.modules   import SoftmaxLayer
from pylab import ion, ioff, figure, draw, contourf, clf, show, hold, plot
from scipy import diag, arange, meshgrid, where
from numpy.random import multivariate_normal

#tstdata, trndata, fnn, trainer = None, None, None, None

def sg_train_ds():
    train_ds=[1.88,1.88]
    return train_ds

def make_training_data():
    means = [(-1,0),(2,4),(3,1)]
    cov = [diag([1,1]), diag([0.5,1.2]), diag([1.5,0.7])]
    alldata = ClassificationDataSet(2, 1, nb_classes=3)
    for n in xrange(400):
        for klass in range(3):
            input = multivariate_normal(means[klass],cov[klass])
            if n==3 and klass == 2:
                print input
            alldata.addSample(input, [klass])
    alldata.addSample(sg_train_ds(), 0)
    return alldata


def set_testing_data():
    tstdata = ClassificationDataSet(2, 1, nb_classes=3)
    tstdata.addSample([7.7393,  2.8826], 0)
    return tstdata  

def converting_data(trndata, tstdata):
    trndata._convertToOneOfMany( )
    tstdata._convertToOneOfMany( )
'''
print "Number of training patterns: ", len(trndata)
print "Input and output dimensions: ", trndata.indim, trndata.outdim
print "First sample (input, target, class):"
print trndata['input'][0], trndata['target'][0], trndata['class'][0]
'''
'''
ticks = arange(-3.,6.,0.2)
X, Y = meshgrid(ticks, ticks)
# need column vectors in dataset, not arrays
griddata = ClassificationDataSet(2,1, nb_classes=3)
for i in xrange(X.size):
    griddata.addSample([X.ravel()[i],Y.ravel()[i]], [0])
griddata._convertToOneOfMany()  # this is still needed to make the fnn feel comfy
'''
def training_ann(trainer):
    for i in range(20):
        trainer.trainEpochs( 1 )
    

def get_percent_error(trainer, tstdata, trndata):
    trnresult = percentError( trainer.testOnClassData(), trndata['class'] )
    tstresult = percentError( trainer.testOnClassData(dataset=tstdata ), tstdata['class'] )
    return tstresult


trndata = make_training_data()
tstdata = set_testing_data()
converting_data(trndata, tstdata)
fnn = buildNetwork( trndata.indim, 5, trndata.outdim, outclass=SoftmaxLayer )
trainer = BackpropTrainer( fnn, dataset=trndata, momentum=0.1, verbose=True, weightdecay=0.01)
training_ann(trainer)
print "  test error: %5.2f%%" % get_percent_error(trainer, tstdata, trndata)
''' 
print "epoch: %4d" % trainer.totalepochs, \
          "  train error: %5.2f%%" % trnresult, \
          "  test error: %5.2f%%" % tstresult
'''
"""          
out = fnn.activateOnDataset(griddata)
out = out.argmax(axis=1)  # the highest output activation gives the class
out = out.reshape(X.shape)

figure(1)
ioff()  # interactive graphics off
clf()   # clear the plot
hold(True) # overplot on
for c in [0,1,2]:
    here, _ = where(tstdata['class']==c)
    plot(tstdata['input'][here,0],tstdata['input'][here,1],'o')
if out.max()!=out.min():  # safety check against flat field
    contourf(X, Y, out)   # plot the contour
ion()   # interactive graphics on
draw()  # update the plot

ioff()
show()
#ds = SupervisedDataSet(2, 1)
#test_ds = ClassificationDataSet(2, 1, nb_classes=3)
#test_ds.addSample((0, 0), (0,))
#test_ds._convertToOneOfMany()

#net = buildNetwork(2, 3, 1, bias=True)

#print net.activate([2, 1])

#ds.addSample((0, 0), (0,))
#ds.addSample((0, 1), (1,))
#ds.addSample((1, 0), (1,))
#ds.addSample((1, 1), (0,))


#trainer = BackpropTrainer(net, ds)


#trainer.trainUntilConvergence()


#trnresult = percentError( trainer.testOnClassData(), ds['class'] )
#print 'trnresult is '+trnresult
#tstresult = percentError( trainer.testOnClassData(dataset=test_ds ), test_ds['class'] )




"""

