# Arduino 0015 Makefile
# Modified by Christopher Rabotin
# Arduino adaptation by mellis, eighthave, oli.keller
#
# Detailed instructions for using the makefile:
#
#  1. Copy this file into the folder with your sketch.
#
#  2. Modify the line containg "INSTALL_DIR" to point to the directory that
#     contains the Arduino installation
#
#  3. Modify the line containing "PORT" to refer to the filename
#     representing the USB or serial connection to your Arduino board
#     (e.g. PORT = /dev/tty.USB0).
#
#  4. Set the line containing "MCU" to match your board's processor. 
#     Older one's are atmega8 based, newer ones like Arduino Mini, Bluetooth
#     or Diecimila have the atmega168.
#
#  5. At the command line, change to the directory containing your
#     program's file and the makefile.
#
#  6. Type "make" and press enter to compile/verify your program.
#
#  7. Type "make upload", reset your Arduino board.

TARGET = Multiprise
OUTPUT_DIR = bin
PORT = /dev/ttyUSB*
CORE_A = 128core.a
INSTALL_DIR = /home/noxon/softs/arduino-0016

UPLOAD_RATE = 19200
AVRDUDE_PROGRAMMER = stk500v1
MCU = atmega128
F_CPU = 16000000
OTHER_C_INCLUDES = ../libXBee/XBee.c
OTHER_CXX_INCLUDES =  XPortCnx.cpp XBeeCnx.cpp
############################################################################
# Below here nothing should be changed...

ARDUINO = $(INSTALL_DIR)/hardware/cores/arduino
AVR_TOOLS_PATH = /opt/cross/bin
SRC =  $(ARDUINO)/pins_arduino.c $(ARDUINO)/wiring.c \
$(ARDUINO)/wiring_analog.c $(ARDUINO)/wiring_digital.c \
$(ARDUINO)/wiring_pulse.c $(ARDUINO)/wiring_serial.c \
$(ARDUINO)/wiring_shift.c $(ARDUINO)/WInterrupts.c \
$(OTHER_C_INCLUDES)

CXXSRC = $(ARDUINO)/HardwareSerial.cpp $(ARDUINO)/WMath.cpp \
$(ARDUINO)/Print.cpp $(OTHER_CXX_INCLUDES)

FORMAT = ihex


# Name of this Makefile (used for "make depend").
MAKEFILE = Makefile

# Debugging format.
# Native formats for AVR-GCC's -g are stabs [default], or dwarf-2.
# AVR (extended) COFF requires stabs, plus an avr-objcopy run.
DEBUG = stabs

OPT = s

# Place -D or -U options here
CDEFS = -DF_CPU=$(F_CPU)
CXXDEFS = -DF_CPU=$(F_CPU)

# Place -I options here
CINCS = -I$(ARDUINO) -I.
CXXINCS = -I$(ARDUINO) -I.

# Compiler flag to set the C Standard level.
# c89   - "ANSI" C
# gnu89 - c89 plus GCC extensions
# c99   - ISO C99 standard (not yet fully implemented)
# gnu99 - c99 plus GCC extensions
CSTANDARD = -std=gnu99
CDEBUG = -g$(DEBUG)
CWARN = -Wall -Wstrict-prototypes
CTUNING = -funsigned-char -funsigned-bitfields -fpack-struct -fshort-enums
#CEXTRA = -Wa,-adhlns=$(<:.c=.lst)

CFLAGS = $(CDEBUG) $(CDEFS) $(CINCS) -O$(OPT) $(CWARN) $(CSTANDARD) $(CEXTRA)
CXXFLAGS = $(CDEFS) $(CINCS) -O$(OPT)
#ASFLAGS = -Wa,-adhlns=$(<:.S=.lst),-gstabs 
LDFLAGS = -lm


# Programming support using avrdude. Settings and variables.
AVRDUDE_PORT = $(PORT)
AVRDUDE_CONF = 
AVRDUDE_WRITE_FLASH = -U flash:w:$(OUTPUT_DIR)/$(TARGET).hex
AVRDUDE_FLAGS = -V -F -C $(INSTALL_DIR)/hardware/tools/avrdude.conf \
-p $(MCU) -P $(AVRDUDE_PORT) -c $(AVRDUDE_PROGRAMMER) \
-b $(UPLOAD_RATE)

# Program settings
CC = $(AVR_TOOLS_PATH)/avr-gcc
CXX = $(AVR_TOOLS_PATH)/avr-g++
OBJCOPY = $(AVR_TOOLS_PATH)/avr-objcopy
OBJDUMP = $(AVR_TOOLS_PATH)/avr-objdump
AR  = $(AVR_TOOLS_PATH)/avr-ar
SIZE = $(AVR_TOOLS_PATH)/avr-size
NM = $(AVR_TOOLS_PATH)/avr-nm
AVRDUDE = $(AVR_TOOLS_PATH)/avrdude
REMOVE = rm -f
MV = mv -f

# Define all object files.
OBJ = $(SRC:.c=.o) $(CXXSRC:.cpp=.o) $(ASRC:.S=.o) 

# Define all listing files.
LST = $(ASRC:.S=.lst) $(CXXSRC:.cpp=.lst) $(SRC:.c=.lst)

# Combine all necessary flags and optional flags.
# Add target processor to flags.
ALL_CFLAGS = -mmcu=$(MCU) -I. $(CFLAGS)
ALL_CXXFLAGS = -mmcu=$(MCU) -I. $(CXXFLAGS)
ALL_ASFLAGS = -mmcu=$(MCU) -I. -x assembler-with-cpp $(ASFLAGS)


# Default target.
all: applet_files build sizeafter

build: elf hex 

applet_files: $(TARGET).pde
	# Here is the "preprocessing".
	# It creates a .cpp file based with the same name as the .pde file.
	# On top of the new .cpp file comes the WProgram.h header.
	# At the end there is a generic main() function attached.
	# Then the .cpp file will be compiled. Errors during compile will
	# refer to this new, automatically generated, file. 
	# Not the original .pde file you actually edit...
	test -d $(OUTPUT_DIR) || mkdir $(OUTPUT_DIR)
	echo '#include "WProgram.h"' > $(TARGET).cpp
#	echo '#include "WProgram.h"' > $(OUTPUT_DIR)/$(TARGET).cpp
#	cat $(TARGET).pde >> $(OUTPUT_DIR)/$(TARGET).cpp
	cat $(TARGET).pde >> $(TARGET).cpp
	cat $(ARDUINO)/main.cxx >> $(TARGET).cpp

elf: $(OUTPUT_DIR)/$(TARGET).elf
hex: $(OUTPUT_DIR)/$(TARGET).hex
eep: $(OUTPUT_DIR)/$(TARGET).eep
lss: $(OUTPUT_DIR)/$(TARGET).lss 
sym: $(OUTPUT_DIR)/$(TARGET).sym

# Program the device.  
upload: $(OUTPUT_DIR)/$(TARGET).hex
	$(AVRDUDE) $(AVRDUDE_FLAGS) $(AVRDUDE_WRITE_FLASH)


	# Display size of file.
HEXSIZE = $(SIZE) --target=$(FORMAT) $(OUTPUT_DIR)/$(TARGET).hex
ELFSIZE = $(SIZE)  $(OUTPUT_DIR)/$(TARGET).elf
sizebefore:
	@if [ -f $(OUTPUT_DIR)/$(TARGET).elf ]; then echo; echo $(MSG_SIZE_BEFORE); $(HEXSIZE); echo; fi

sizeafter:
	@if [ -f $(OUTPUT_DIR)/$(TARGET).elf ]; then echo; echo $(MSG_SIZE_AFTER); $(HEXSIZE); echo; fi


# Convert ELF to COFF for use in debugging / simulating in AVR Studio or VMLAB.
COFFCONVERT=$(OBJCOPY) --debugging \
--change-section-address .data-0x800000 \
--change-section-address .bss-0x800000 \
--change-section-address .noinit-0x800000 \
--change-section-address .eeprom-0x810000 


coff: $(OUTPUT_DIR)/$(TARGET).elf
	$(COFFCONVERT) -O coff-avr $(OUTPUT_DIR)/$(TARGET).elf $(TARGET).cof


extcoff: $(TARGET).elf
	$(COFFCONVERT) -O coff-ext-avr $(OUTPUT_DIR)/$(TARGET).elf $(TARGET).cof


.SUFFIXES: .elf .hex .eep .lss .sym

.elf.hex:
	$(OBJCOPY) -O $(FORMAT) -R .eeprom $< $@

.elf.eep:
	-$(OBJCOPY) -j .eeprom --set-section-flags=.eeprom="alloc,load" \
	--change-section-lma .eeprom=0 -O $(FORMAT) $< $@

# Create extended listing file from ELF output file.
.elf.lss:
	$(OBJDUMP) -h -S $< > $@

# Create a symbol table from ELF output file.
.elf.sym:
	$(NM) -n $< > $@

	# Link: create ELF output file from library.
$(OUTPUT_DIR)/$(TARGET).elf: $(TARGET).pde $(OUTPUT_DIR)/$(CORE_A) 
	$(CC) $(ALL_CFLAGS) -o $@ $(TARGET).cpp -L. $(OUTPUT_DIR)/$(CORE_A) $(LDFLAGS)

$(OUTPUT_DIR)/$(CORE_A): $(OBJ)
	@for i in $(OBJ); do echo $(AR) rcs $(OUTPUT_DIR)/$(CORE_A) $$i; $(AR) rcs $(OUTPUT_DIR)/$(CORE_A) $$i; done



# Compile: create object files from C++ source files.
.cpp.o:
	$(CXX) -c $(ALL_CXXFLAGS) $< -o $@ 

# Compile: create object files from C source files.
.c.o:
	$(CC) -c $(ALL_CFLAGS) $< -o $@ 


# Compile: create assembler files from C source files.
.c.s:
	$(CC) -S $(ALL_CFLAGS) $< -o $@


# Assemble: create object files from assembler source files.
.S.o:
	$(CC) -c $(ALL_ASFLAGS) $< -o $@


# Automatic dependencies
%.d: %.c
	$(CC) -M $(ALL_CFLAGS) $< | sed "s;$(notdir $*).o:;$*.o $*.d:;" > $@

%.d: %.cpp
	$(CXX) -M $(ALL_CXXFLAGS) $< | sed "s;$(notdir $*).o:;$*.o $*.d:;" > $@


# Target: clean project.
clean:
	$(REMOVE) $(OUTPUT_DIR)/$(TARGET).hex $(OUTPUT_DIR)/$(TARGET).eep $(OUTPUT_DIR)/$(TARGET).cof $(OUTPUT_DIR)/$(TARGET).elf \
	$(OUTPUT_DIR)/$(TARGET).map $(OUTPUT_DIR)/$(TARGET).sym $(OUTPUT_DIR)/$(TARGET).lss $(OUTPUT_DIR)/$(CORE_A) $(TARGET).cpp
#	$(OBJ) $(LST) $(SRC:.c=.s) $(SRC:.c=.d) $(CXXSRC:.cpp=.s) $(CXXSRC:.cpp=.d)

.PHONY:	all build elf hex eep lss sym program coff extcoff clean applet_files sizebefore sizeafter

#include $(SRC:.c=.d)
#include $(CXXSRC:.cpp=.d)
