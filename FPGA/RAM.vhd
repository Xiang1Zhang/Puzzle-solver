library IEEE;
use IEEE.std_logic_1164.all;
use ieee.numeric_std.all;
use work.all;

entity RAM is
	port(
		-- System signals
		s_reset		: in std_logic;
		s_clk		: in std_logic;
		
		-- Read
		r_dat		: out std_logic_vector(7 downto 0);
		r_adr		: in std_logic_vector(16 downto 0);
		
		-- Write
		w_dat		: in std_logic_vector(7 downto 0);
		w_adr		: in std_logic_vector(16 downto 0);
		w_wr		: in std_logic
	);
end entity;

architecture A of RAM is

	-- Internal buffers
	signal i_r_dat 		: std_logic_vector(7 downto 0);

	-- Read
	signal q0, q1		: std_logic_vector(7 downto 0);
	signal r_cs0, r_cs1	: std_logic;

	-- Write
	signal w_cs0, w_cs1	: std_logic;

begin

	-- Mapping
	r_dat <= i_r_dat;

	e_r0 : entity DPRAM(SYN) port map(
		s_clk,
		w_dat,
		r_adr(15 downto 0),
		w_adr(15 downto 0),
		w_cs0,
		q0
	);
	e_r1 : entity DPRAM(SYN) port map(
		s_clk,
		w_dat,
		r_adr(15 downto 0),
		w_adr(15 downto 0),
		w_cs1,
		q1
	);
	
	w_cs0 <= (not w_adr(16)) and w_wr;
	w_cs1 <= w_adr(16) and w_wr;
	
	r_cs0 <= not r_adr(16);
	r_cs1 <= r_adr(16);
	
	i_r_dat <= q0 when r_cs0='1' else q1 when r_cs1='1';
	
end architecture;
