/*
  Serial.c - Serial library for Wiring
  Based on Hernando Barragan's original C implementation
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

#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>
#include <avr/io.h>
#include <avr/interrupt.h>

#include "buffer.h"
#include "uart.h"

/*******************************************************************************
 UART API
*******************************************************************************/

buffer_t uart0_rxBuffer;
buffer_t uart1_rxBuffer;
buffer_t uart0_txBuffer;
buffer_t uart1_txBuffer;

static char uart0_rxData[UART_BUFFER_LENGTH];
static char uart0_txData[UART_BUFFER_LENGTH];

static char uart1_rxData[UART_BUFFER_LENGTH];
static char uart1_txData[UART_BUFFER_LENGTH];

//char* uart1_rxData;
//char* uart1_txData;

void uart_init(uint8_t uart, uint32_t baudrate)
{
  uint16_t t;
  t = ((CPU_FREQ % (16L*baudrate) >= (16L*baudrate)/2) ? (CPU_FREQ / (16L*baudrate)) : (CPU_FREQ / (16L*baudrate)) - 1);
  if(0 == uart){
    // enable UART0
    //UBRR0H = ((CPU_FREQ/(16L*baudrate) - 1) >> 8) & 0xff;
    //UBRR0L = (CPU_FREQ/(16L*baudrate) - 1) & 0xff;
    UBRR0H  = (t >> 8) & 0xff;
    UBRR0L  = t & 0xff;
    UCSR0B = _BV(RXEN)|_BV(TXEN)|_BV(RXCIE)|_BV(TXCIE);
    // init buffers
    buffer_init(&uart0_rxBuffer, uart0_rxData, sizeof(uart0_rxData));
    buffer_init(&uart0_txBuffer, uart0_txData, sizeof(uart0_txData));
  }else{
    // enable UART1
    //UBRR1H = ((CPU_FREQ/(16L*baudrate) - 1) >> 8) & 0xff;
    //UBRR1L = (CPU_FREQ/(16L*baudrate) - 1) & 0xff;
    UBRR1H  = (t >> 8) & 0xff;
    UBRR1L  = t & 0xff;
    UCSR1B = _BV(RXEN)|_BV(TXEN)|_BV(RXCIE)|_BV(TXCIE);
    // allocate and init buffers
    //uart1_rxData = (char*) malloc(UART_BUFFER_LENGTH);
    //uart1_txData = (char*) malloc(UART_BUFFER_LENGTH);
    buffer_init(&uart1_rxBuffer, uart1_rxData, sizeof(uart1_rxData));
    buffer_init(&uart1_txBuffer, uart1_txData, sizeof(uart1_txData));
  }
}

int uart_read(uint8_t uart)
{
  if(0 == uart){
    return buffer_get(&uart0_rxBuffer);
  }
  return buffer_get(&uart1_rxBuffer);
}

uint8_t uart_available(uint8_t uart)
{
  if(0 == uart){
    return buffer_available(&uart0_rxBuffer);
  }
  return buffer_available(&uart1_rxBuffer);
}

void uart_write(uint8_t uart, char *buf, uint8_t len)
{
  uint8_t i;
  uint8_t ints;
  // return if nothing to send
  if (len == 0){
    return;
  }
  // record interrupts
  ints = SREG & 0x80;
  // clear interrupts
  cli();
  // prepare for interrupt handler
  if(0 == uart){
    // append to tx buffer
    for(i = 0; i < len; ++i){
      buffer_put(&uart0_txBuffer, buf[i]);
    }
    // transmit first byte
    UCSR0A |= _BV(TXC0);
    while (bit_is_clear(UCSR0A, UDRE)){
      continue;
    }
    UDR0 = buffer_get(&uart0_txBuffer);
  }else{
    // append to tx buffer
    for(i = 0; i < len; ++i){
      buffer_put(&uart1_txBuffer, buf[i]);
    }
    // transmit first byte
    UCSR1A |= _BV(TXC1);
    while (bit_is_clear(UCSR1A, UDRE)){
      continue;
    }
    UDR1 = buffer_get(&uart1_txBuffer);
  }
  // reenable interrupts
  SREG |= ints;
}

void uart_flush(uint8_t uart)
{
  if(0 == uart){
    buffer_init(&uart0_rxBuffer, uart0_rxData, sizeof(uart0_rxData));
    buffer_init(&uart0_txBuffer, uart0_txData, sizeof(uart0_txData));
  }else{
    buffer_init(&uart1_rxBuffer, uart1_rxData, sizeof(uart1_rxData));
    buffer_init(&uart1_txBuffer, uart1_txData, sizeof(uart1_txData));
  }
}


/*******************************************************************************
 Interrupt Handlers
*******************************************************************************/

// UART0 byte received interrupt handler 
SIGNAL(SIG_UART0_RECV)
{
  char c;
  // fetch incoming character
  c = UDR0;
  // check for framing error
  if (bit_is_clear(UCSR0A, FE)) {
    // put character in RX buffer
    buffer_put(&uart0_rxBuffer, c);
  }
}

// UART1 byte received interrupt handler
SIGNAL(SIG_UART1_RECV)
{
  char c;
  // fetch incoming character
  c = UDR1;
  // check for framing error
  if (bit_is_clear(UCSR1A, FE)) {
    // put character in RX buffer
    buffer_put(&uart1_rxBuffer, c);
  }
}

// UART0 transmit complete interrupt
SIGNAL(SIG_UART0_TRANS)
{
  // return if nothing left to send
  if (0 == buffer_available(&uart0_txBuffer)){
    return;
  }
  // grab character from TX buffer
  UDR0 = buffer_get(&uart0_txBuffer);
}

// UART1 transmit complete interrupt
SIGNAL(SIG_UART1_TRANS)
{
  // return if nothing left to send
  if (0 == buffer_available(&uart1_txBuffer)){
    return;
  }
  // grab character from TX buffer
  UDR1 = buffer_get(&uart1_txBuffer);
}

