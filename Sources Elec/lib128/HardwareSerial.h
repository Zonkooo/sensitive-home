/*
  HardwareSerial.h - Hardware serial library for Wiring
  Copyright (c) 2006 Nicholas Zambetti.  All right reserved.

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

#ifndef HardwareSerial_h
#define HardwareSerial_h

#include <inttypes.h>
#include "WPrint.h"

/*
#define DEC 10
#define HEX 16
#define OCT 8
#define BIN 2
#define BYTE 0
*/

class HardwareSerial : public Print
{
  private:
    uint8_t _uart;
//    void printNumber(unsigned long, uint8_t);
  public:
    HardwareSerial(uint8_t);
    void begin(uint32_t);
    uint8_t available(void);
    int read(void);
    void flush(void);
    virtual void write(uint8_t);


    void print(char);
    void print(char[]);
    //void print(String &) ;
    void print(uint8_t);
    void print(int);
    void print(long);
    void print(long, int);
    void println(void);
    void println(char);
    void println(char[]);
    //void println(String &);
    void println(uint8_t);
    void println(int);
    void println(long);
    void println(long, int);
    void printNumber(unsigned long n, uint8_t base);

};

extern HardwareSerial Serial;
extern HardwareSerial Serial1;

#endif

